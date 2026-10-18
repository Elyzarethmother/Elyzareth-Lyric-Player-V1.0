package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerEngine
import com.example.data.ProjectStorage
import com.example.model.ActiveTab
import com.example.model.AspectRatioMode
import com.example.model.DefaultSongLibrary
import com.example.model.DspSettings
import com.example.model.FitLayoutMode
import com.example.model.LyricLine
import com.example.model.PlayerSettings
import com.example.model.RepeatMode
import com.example.model.Song
import com.example.model.ThemeChoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LyricPlayerViewModel(application: Application) : AndroidViewModel(application) {
    val audioEngine = AudioPlayerEngine(application)
    private val storage = ProjectStorage(application)

    // Empty by default or restored from saved user session
    private val _playlist = MutableStateFlow<List<Song>>(storage.loadPlaylist())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(_playlist.value.firstOrNull())
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _aspectRatio = MutableStateFlow(storage.getSavedAspectRatio())
    val aspectRatio: StateFlow<AspectRatioMode> = _aspectRatio.asStateFlow()

    private val _themeChoice = MutableStateFlow(storage.getSavedTheme())
    val themeChoice: StateFlow<ThemeChoice> = _themeChoice.asStateFlow()

    private val _isDarkMode = MutableStateFlow(storage.getSavedIsDark())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _fitMode = MutableStateFlow(FitLayoutMode.COVER)
    val fitMode: StateFlow<FitLayoutMode> = _fitMode.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    private val _isPlaylistVisible = MutableStateFlow(true)
    val isPlaylistVisible: StateFlow<Boolean> = _isPlaylistVisible.asStateFlow()

    private val _isSingleSongMode = MutableStateFlow(false)
    val isSingleSongMode: StateFlow<Boolean> = _isSingleSongMode.asStateFlow()

    private val _isAutoNext = MutableStateFlow(storage.getSavedAutoNext())
    val isAutoNext: StateFlow<Boolean> = _isAutoNext.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _activeTab = MutableStateFlow(ActiveTab.PLAYLIST)
    val activeTab: StateFlow<ActiveTab> = _activeTab.asStateFlow()

    private val _dspSettings = MutableStateFlow(DspSettings())
    val dspSettings: StateFlow<DspSettings> = _dspSettings.asStateFlow()

    private val _playerSettings = MutableStateFlow(PlayerSettings())
    val playerSettings: StateFlow<PlayerSettings> = _playerSettings.asStateFlow()

    // Overlay dialog triggers
    val isThemeMenuOpen = MutableStateFlow(false)
    val isDspMenuOpen = MutableStateFlow(false)
    val isSettingsOpen = MutableStateFlow(false)
    val isSearchOpen = MutableStateFlow(false)
    val isMoreMenuOpen = MutableStateFlow(false)
    val isQueueOpen = MutableStateFlow(false)
    val isLyricsEditorOpen = MutableStateFlow(false)
    val isAuditInspectorOpen = MutableStateFlow(false)

    init {
        _currentSong.value?.let {
            audioEngine.loadSong(it)
        }
        audioEngine.setOnCompletionListener {
            handleSongCompleted()
        }
    }

    private fun handleSongCompleted() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                audioEngine.seekTo(0L)
                audioEngine.play()
            }
            RepeatMode.ALL -> {
                playNextSong()
            }
            RepeatMode.OFF -> {
                if (_isAutoNext.value) {
                    playNextSong()
                }
            }
        }
    }

    // [01] Playlist Mode
    fun togglePlaylistMode() {
        _isPlaylistVisible.value = !_isPlaylistVisible.value
    }

    // [04] Top Favorite and [17] Playback Favorite share this exact state
    fun toggleFavoriteCurrentSong() {
        val cur = _currentSong.value ?: return
        val updated = cur.copy(isFavorite = !cur.isFavorite)
        _currentSong.value = updated
        _playlist.value = _playlist.value.map { if (it.id == updated.id) updated else it }
        storage.savePlaylist(_playlist.value)
    }

    // [06] Theme Menu
    fun setTheme(theme: ThemeChoice) {
        _themeChoice.value = theme
        storage.savePreferences(theme, _isDarkMode.value, _aspectRatio.value, _isAutoNext.value)
    }

    // [11] Light Theme
    fun setLightTheme() {
        _isDarkMode.value = false
        storage.savePreferences(_themeChoice.value, false, _aspectRatio.value, _isAutoNext.value)
    }

    // [12] Dark Theme
    fun setDarkTheme() {
        _isDarkMode.value = true
        storage.savePreferences(_themeChoice.value, true, _aspectRatio.value, _isAutoNext.value)
    }

    // [13] Fit Layout
    fun cycleFitLayout() {
        val values = FitLayoutMode.values()
        val nextIdx = (values.indexOf(_fitMode.value) + 1) % values.size
        _fitMode.value = values[nextIdx]
    }

    // [14] Fullscreen
    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    // [15] Recording aspect ratio
    fun toggleAspectRatio() {
        val newRatio = if (_aspectRatio.value == AspectRatioMode.RATIO_9_16) {
            AspectRatioMode.RATIO_16_9
        } else {
            AspectRatioMode.RATIO_9_16
        }
        _aspectRatio.value = newRatio
        storage.savePreferences(_themeChoice.value, _isDarkMode.value, newRatio, _isAutoNext.value)
    }

    // [16] Seek bar
    fun seekTo(positionMs: Long) {
        audioEngine.seekTo(positionMs)
    }

    // [18] Shuffle
    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    // [19] Previous track
    fun playPreviousSong() {
        val list = _playlist.value
        if (list.isEmpty()) return
        val cur = _currentSong.value
        val curIdx = if (cur != null) list.indexOfFirst { it.id == cur.id } else 0
        val prevIdx = if (curIdx <= 0) list.size - 1 else curIdx - 1
        selectSong(list[prevIdx])
    }

    // [20] Play/Pause
    fun togglePlayPause() {
        if (_currentSong.value == null && _playlist.value.isNotEmpty()) {
            selectSong(_playlist.value.first())
        }
        audioEngine.togglePlayPause()
    }

    // [21] Next track
    fun playNextSong() {
        val list = _playlist.value
        if (list.isEmpty()) return
        val cur = _currentSong.value
        val curIdx = if (cur != null) list.indexOfFirst { it.id == cur.id } else 0
        val nextIdx = if (_isShuffle.value && list.size > 1) {
            val remaining = list.indices.filter { it != curIdx }
            remaining.random()
        } else {
            (curIdx + 1) % list.size
        }
        selectSong(list[nextIdx])
    }

    // [22] Repeat
    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    // [24], [25], [26] Tabs
    fun setActiveTab(tab: ActiveTab) {
        _activeTab.value = tab
    }

    // [28] Single Song Mode
    fun toggleSingleSongMode() {
        _isSingleSongMode.value = !_isSingleSongMode.value
    }

    // [29] Auto Next
    fun toggleAutoNext() {
        val nextVal = !_isAutoNext.value
        _isAutoNext.value = nextVal
        storage.savePreferences(_themeChoice.value, _isDarkMode.value, _aspectRatio.value, nextVal)
    }

    // [31] - [36] Select Song
    fun selectSong(song: Song) {
        val wasPlaying = audioEngine.isPlaying.value
        _currentSong.value = song
        audioEngine.loadSong(song)
        if (wasPlaying) {
            audioEngine.play()
        }
    }

    // [09] DSP update
    fun updateDsp(settings: DspSettings) {
        _dspSettings.value = settings
        audioEngine.updateDsp(settings)
    }

    // [10] Settings update
    fun updatePlayerSettings(settings: PlayerSettings) {
        _playerSettings.value = settings
    }

    // [25] Edit lyrics & save
    fun saveSongLyrics(newLyrics: List<LyricLine>) {
        val cur = _currentSong.value ?: return
        val updated = cur.copy(lyrics = newLyrics)
        _currentSong.value = updated
        _playlist.value = _playlist.value.map { if (it.id == updated.id) updated else it }
        storage.savePlaylist(_playlist.value)
    }

    // [05] More: Import Background Image
    fun setCustomBackground(uri: Uri) {
        val cur = _currentSong.value
        if (cur != null) {
            val updated = cur.copy(backgroundUri = uri)
            _currentSong.value = updated
            _playlist.value = _playlist.value.map { if (it.id == updated.id) updated else it }
            storage.savePlaylist(_playlist.value)
        }
    }

    // [05] More: Remove current song
    fun removeCurrentSong() {
        val cur = _currentSong.value ?: return
        val remaining = _playlist.value.filter { it.id != cur.id }
        _playlist.value = remaining
        storage.savePlaylist(remaining)
        if (remaining.isNotEmpty()) {
            selectSong(remaining.first())
        } else {
            audioEngine.stop()
            _currentSong.value = null
        }
    }

    // [05] More: Clear entire playlist
    fun clearPlaylist() {
        audioEngine.stop()
        _playlist.value = emptyList()
        _currentSong.value = null
        storage.savePlaylist(emptyList())
    }

    // [27] Add Songs (load local audio file)
    fun addLocalAudio(uri: Uri, displayName: String, durationMs: Long) {
        val newSong = Song(
            id = "user_${System.currentTimeMillis()}",
            title = displayName.substringBeforeLast("."),
            artist = "Local Artist",
            durationMs = durationMs,
            lyrics = emptyList(),
            isFavorite = false,
            fileUri = uri
        )
        val updated = _playlist.value + newSong
        _playlist.value = updated
        storage.savePlaylist(updated)
        selectSong(newSong)
    }

    // Close all open modals & popups
    fun closeAllOverlays() {
        isThemeMenuOpen.value = false
        isDspMenuOpen.value = false
        isSettingsOpen.value = false
        isSearchOpen.value = false
        isMoreMenuOpen.value = false
        isQueueOpen.value = false
        isLyricsEditorOpen.value = false
        isAuditInspectorOpen.value = false
    }

    fun hasAnyOpenOverlay(): Boolean {
        return isThemeMenuOpen.value ||
                isDspMenuOpen.value ||
                isSettingsOpen.value ||
                isSearchOpen.value ||
                isMoreMenuOpen.value ||
                isQueueOpen.value ||
                isLyricsEditorOpen.value ||
                isAuditInspectorOpen.value
    }

    // Exit Fullscreen mode
    fun exitFullscreen() {
        _isFullscreen.value = false
    }

    // Load rich demo track for instant testing without needing local files
    fun loadDemoSong() {
        val demo = DefaultSongLibrary.demoSong
        val currentList = _playlist.value
        val alreadyExists = currentList.any { it.id == demo.id }
        if (!alreadyExists) {
            val updated = currentList + demo
            _playlist.value = updated
            storage.savePlaylist(updated)
            selectSong(demo)
        } else {
            selectSong(demo)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
