package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.util.Log
import com.example.model.DspSettings
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

class AudioPlayerEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private var mediaPlayer: MediaPlayer? = null
    private var equalizer: Equalizer? = null
    private var tickerJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isEqSupported = MutableStateFlow(true)
    val isEqSupported: StateFlow<Boolean> = _isEqSupported.asStateFlow()

    private var currentSong: Song? = null
    private var currentDsp: DspSettings = DspSettings()
    private var onCompletionCallback: (() -> Unit)? = null

    init {
        startTicker()
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionCallback = listener
    }

    fun loadSong(song: Song) {
        stop()
        currentSong = song
        _currentPositionMs.value = 0L

        val uri = song.fileUri
        if (uri != null) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(context, uri)
                    prepare()
                    _durationMs.value = duration.toLong().coerceAtLeast(0L)
                    setOnCompletionListener {
                        _isPlaying.value = false
                        onCompletionCallback?.invoke()
                    }
                }
                setupEqualizer(mediaPlayer?.audioSessionId ?: 0)
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Failed to load audio from URI $uri", e)
                mediaPlayer?.release()
                mediaPlayer = null
                _durationMs.value = song.durationMs
            }
        } else {
            _durationMs.value = song.durationMs
        }
    }

    fun play() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.start()
                _isPlaying.value = true
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Error starting playback", e)
            }
        }
    }

    fun pause() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.pause()
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Error pausing playback", e)
            }
        }
        _isPlaying.value = false
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(positionMs: Long) {
        val target = positionMs.coerceIn(0L, _durationMs.value)
        _currentPositionMs.value = target
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.seekTo(target.toInt())
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Error seeking to $target", e)
            }
        }
    }

    fun stop() {
        _isPlaying.value = false
        releaseEqualizer()
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
            } catch (_: Exception) {}
            it.release()
        }
        mediaPlayer = null
        _currentPositionMs.value = 0L
    }

    private fun setupEqualizer(audioSessionId: Int) {
        if (audioSessionId == 0) return
        releaseEqualizer()
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = currentDsp.isEnabled
                applyEqualizerBands(this, currentDsp)
            }
            _isEqSupported.value = true
        } catch (e: Exception) {
            Log.w("AudioPlayerEngine", "Hardware Equalizer not supported on this session", e)
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

    fun updateDsp(settings: DspSettings) {
        currentDsp = settings
        equalizer?.let { eq ->
            try {
                eq.enabled = settings.isEnabled
                if (settings.isEnabled) {
                    applyEqualizerBands(eq, settings)
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Error applying DSP settings", e)
            }
        }
    }

    private fun applyEqualizerBands(eq: Equalizer, settings: DspSettings) {
        val numBands = eq.numberOfBands.toInt()
        val minLevel = eq.bandLevelRange[0] // e.g. -1500 mB
        val maxLevel = eq.bandLevelRange[1] // e.g. +1500 mB

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

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                if (_isPlaying.value && mediaPlayer != null) {
                    try {
                        val pos = mediaPlayer?.currentPosition?.toLong() ?: 0L
                        _currentPositionMs.value = pos
                    } catch (_: Exception) {}
                }
                delay(60L)
            }
        }
    }

    fun release() {
        stop()
        tickerJob?.cancel()
    }
}
