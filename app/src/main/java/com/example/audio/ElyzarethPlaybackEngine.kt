package com.example.audio

import android.content.Context
import android.media.audiofx.Equalizer
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.ProjectStorage
import com.example.model.DefaultSongLibrary
import com.example.model.DspSettings
import com.example.model.RepeatMode
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ElyzarethPlaybackEngine private constructor(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private val storage = ProjectStorage(context)

    val player: ExoPlayer

    private var equalizer: Equalizer? = null
    private var tickerJob: Job? = null
    private var currentDsp: DspSettings = DspSettings()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isEqSupported = MutableStateFlow(true)
    val isEqSupported: StateFlow<Boolean> = _isEqSupported.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private var onSongCompletionCallback: (() -> Unit)? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        setupPlayerListener()
        startPositionTicker()

        // Restore playlist and last session
        val savedList = storage.loadPlaylist()
        _playlist.value = savedList
        val lastSongId = storage.getLastSongId()
        val initialSong = savedList.find { it.id == lastSongId } ?: savedList.firstOrNull() ?: DefaultSongLibrary.demoSong
        _currentSong.value = initialSong
        _durationMs.value = initialSong.durationMs
        prepareMediaItemForSong(initialSong, playWhenReady = false)

        val lastPos = storage.getLastPositionMs()
        if (lastPos > 0 && lastPos < initialSong.durationMs) {
            seekTo(lastPos)
        }
    }

    private fun setupPlayerListener() {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) {
                    saveState()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        _durationMs.value = player.duration.coerceAtLeast(0L)
                    }
                    Player.STATE_ENDED -> {
                        _isPlaying.value = false
                        onSongCompletionCallback?.invoke()
                    }
                    Player.STATE_IDLE, Player.STATE_BUFFERING -> {}
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaItem != null) {
                    val matching = _playlist.value.find { it.id == mediaItem.mediaId }
                    if (matching != null && matching.id != _currentSong.value?.id) {
                        _currentSong.value = matching
                        _durationMs.value = if (matching.durationMs > 0) matching.durationMs else player.duration.coerceAtLeast(0L)
                        saveState()
                    }
                }
            }
        })
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onSongCompletionCallback = listener
    }

    fun setPlaylist(songs: List<Song>, autoSelect: Song? = null) {
        _playlist.value = songs
        storage.savePlaylist(songs)
        syncExoPlayerPlaylist(songs)

        val target = autoSelect ?: songs.firstOrNull()
        if (target != null && target.id != _currentSong.value?.id) {
            selectSong(target, playWhenReady = false)
        }
    }

    private fun syncExoPlayerPlaylist(songs: List<Song>) {
        val mediaItems = songs.map { it.toMediaItem() }
        player.setMediaItems(mediaItems, false)
        player.prepare()
    }

    fun selectSong(song: Song, playWhenReady: Boolean = true) {
        _currentSong.value = song
        _currentPositionMs.value = 0L
        _durationMs.value = song.durationMs

        // Find index in playlist
        val idx = _playlist.value.indexOfFirst { it.id == song.id }
        if (idx >= 0 && idx < player.mediaItemCount) {
            player.seekTo(idx, 0L)
            player.playWhenReady = playWhenReady
            if (playWhenReady) {
                player.play()
            }
        } else {
            prepareMediaItemForSong(song, playWhenReady)
        }
        setupEqualizer(player.audioSessionId)
        saveState()
    }

    private fun prepareMediaItemForSong(song: Song, playWhenReady: Boolean) {
        val item = song.toMediaItem()
        player.setMediaItem(item)
        player.prepare()
        player.playWhenReady = playWhenReady
        if (playWhenReady) {
            player.play()
        }
        setupEqualizer(player.audioSessionId)
    }

    fun play() {
        if (_currentSong.value == null && _playlist.value.isNotEmpty()) {
            selectSong(_playlist.value.first(), playWhenReady = true)
            return
        }
        player.play()
        _isPlaying.value = true
        setupEqualizer(player.audioSessionId)
    }

    fun pause() {
        player.pause()
        _isPlaying.value = false
        saveState()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(positionMs: Long) {
        val maxDuration = if (_durationMs.value > 0) _durationMs.value else player.duration.coerceAtLeast(0L)
        val target = if (maxDuration > 0) positionMs.coerceIn(0L, maxDuration) else positionMs
        _currentPositionMs.value = target
        player.seekTo(target)
        saveState()
    }

    fun playNext() {
        val list = _playlist.value
        if (list.isEmpty()) return
        val cur = _currentSong.value
        val curIdx = if (cur != null) list.indexOfFirst { it.id == cur.id } else 0
        val nextIdx = if (_isShuffle.value && list.size > 1) {
            val others = list.indices.filter { it != curIdx }
            others.random()
        } else {
            (curIdx + 1) % list.size
        }
        selectSong(list[nextIdx], playWhenReady = true)
    }

    fun playPrevious() {
        val list = _playlist.value
        if (list.isEmpty()) return
        val cur = _currentSong.value
        val curIdx = if (cur != null) list.indexOfFirst { it.id == cur.id } else 0
        val prevIdx = if (curIdx <= 0) list.size - 1 else curIdx - 1
        selectSong(list[prevIdx], playWhenReady = true)
    }

    fun stop() {
        player.stop()
        _isPlaying.value = false
        _currentPositionMs.value = 0L
        releaseEqualizer()
    }

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
        player.repeatMode = when (mode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
    }

    fun setShuffle(enabled: Boolean) {
        _isShuffle.value = enabled
        player.shuffleModeEnabled = enabled
    }

    fun updateDsp(settings: DspSettings) {
        currentDsp = settings
        equalizer?.let { eq ->
            try {
                eq.enabled = settings.isEnabled
                if (settings.isEnabled) {
                    applyEqualizerBands(eq, settings)
                }
            } catch (e: Exception) {
                Log.e("ElyzarethPlaybackEngine", "Error applying DSP: ${e.message}")
            }
        }
    }

    private fun setupEqualizer(sessionId: Int) {
        if (sessionId == C.AUDIO_SESSION_ID_UNSET || sessionId == 0) return
        releaseEqualizer()
        try {
            equalizer = Equalizer(0, sessionId).apply {
                enabled = currentDsp.isEnabled
                applyEqualizerBands(this, currentDsp)
            }
            _isEqSupported.value = true
        } catch (e: Exception) {
            Log.w("ElyzarethPlaybackEngine", "Equalizer not supported on session $sessionId")
            _isEqSupported.value = false
            equalizer = null
        }
    }

    private fun releaseEqualizer() {
        try {
            equalizer?.enabled = false
            equalizer?.release()
        } catch (_: Exception) {}
        equalizer = null
    }

    private fun applyEqualizerBands(eq: Equalizer, settings: DspSettings) {
        val numBands = eq.numberOfBands.toInt()
        val minLevel = eq.bandLevelRange[0]
        val maxLevel = eq.bandLevelRange[1]

        val bandGains = listOf(
            settings.band60Hz,
            settings.band230Hz,
            settings.band910Hz,
            settings.band3kHz,
            settings.band14kHz
        )

        for (i in 0 until numBands) {
            val gainDb = bandGains.getOrElse(i) { 0f }
            val mB = (gainDb * 100).toInt().coerceIn(minLevel.toInt(), maxLevel.toInt())
            try {
                eq.setBandLevel(i.toShort(), mB.toShort())
            } catch (_: Exception) {}
        }
    }

    private fun startPositionTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                if (player.isPlaying) {
                    _currentPositionMs.value = player.currentPosition.coerceAtLeast(0L)
                    val dur = player.duration
                    if (dur > 0) {
                        _durationMs.value = dur
                    }
                }
                delay(50L) // 20 updates per second for smooth lyrics and seek bar
            }
        }
    }

    private fun saveState() {
        val song = _currentSong.value
        val pos = player.currentPosition
        if (song != null) {
            storage.saveLastPlaybackState(song.id, pos)
        }
    }

    fun release() {
        saveState()
        tickerJob?.cancel()
        releaseEqualizer()
        player.release()
    }

    companion object {
        @Volatile
        private var INSTANCE: ElyzarethPlaybackEngine? = null

        fun getInstance(context: Context): ElyzarethPlaybackEngine {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ElyzarethPlaybackEngine(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
