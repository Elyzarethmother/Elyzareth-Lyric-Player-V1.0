package com.example.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatioMode
import com.example.ui.components.ControlAuditInspectorOverlay
import com.example.ui.components.DspEqOverlay
import com.example.ui.components.LyricsTimingEditorOverlay
import com.example.ui.components.LyricsView
import com.example.ui.components.MoreSongMenuOverlay
import com.example.ui.components.OrnateArtworkCanvas
import com.example.ui.components.OrnatePlayerFrame
import com.example.ui.components.PlaybackControls
import com.example.ui.components.PlaylistSection
import com.example.ui.components.QueueOverlay
import com.example.ui.components.SearchOverlay
import com.example.ui.components.SettingsOverlay
import com.example.ui.components.SettingsRail
import com.example.ui.components.ThemeMenuOverlay
import com.example.ui.components.TopControlsBar
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright
import com.example.ui.theme.ElyzarethTheme
import com.example.viewmodel.LyricPlayerViewModel

@Composable
fun ElyzarethPlayerApp(
    viewModel: LyricPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Observables
    val playlist by viewModel.playlist.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.audioEngine.isPlaying.collectAsState()
    val currentPositionMs by viewModel.audioEngine.currentPositionMs.collectAsState()
    val durationMs by viewModel.audioEngine.durationMs.collectAsState()
    val isEqSupported by viewModel.audioEngine.isEqSupported.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    val themeChoice by viewModel.themeChoice.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val fitMode by viewModel.fitMode.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()
    val isPlaylistVisible by viewModel.isPlaylistVisible.collectAsState()
    val isSingleSongMode by viewModel.isSingleSongMode.collectAsState()
    val isAutoNext by viewModel.isAutoNext.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val dspSettings by viewModel.dspSettings.collectAsState()
    val playerSettings by viewModel.playerSettings.collectAsState()

    // Overlay visibility states
    val isThemeMenuOpen by viewModel.isThemeMenuOpen.collectAsState()
    val isDspMenuOpen by viewModel.isDspMenuOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()
    val isMoreMenuOpen by viewModel.isMoreMenuOpen.collectAsState()
    val isQueueOpen by viewModel.isQueueOpen.collectAsState()
    val isLyricsEditorOpen by viewModel.isLyricsEditorOpen.collectAsState()
    val isAuditInspectorOpen by viewModel.isAuditInspectorOpen.collectAsState()

    val anyOverlayOpen = isThemeMenuOpen ||
            isDspMenuOpen ||
            isSettingsOpen ||
            isSearchOpen ||
            isMoreMenuOpen ||
            isQueueOpen ||
            isLyricsEditorOpen ||
            isAuditInspectorOpen

    var lastBackPressTime by remember { mutableStateOf(0L) }

    // Tier 1: When any modal/popup is open, pressing Android Back dismisses all open modals!
    BackHandler(enabled = anyOverlayOpen) {
        viewModel.closeAllOverlays()
    }

    // Tier 2: When in Fullscreen mode and no modal is open, pressing Android Back exits Fullscreen!
    BackHandler(enabled = !anyOverlayOpen && isFullscreen) {
        viewModel.exitFullscreen()
    }

    // Tier 3: When on main screen (no modal, not in fullscreen), double Back press with Toast confirmation exits app!
    BackHandler(enabled = !anyOverlayOpen && !isFullscreen) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000L) {
            (context as? android.app.Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(context, "Press back again to exit ELYZARETH", Toast.LENGTH_SHORT).show()
        }
    }

    // [27] Add Songs launcher (Imports one or multiple local audio files)
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                var fileName = "Imported Audio"
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}

                try {
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIndex >= 0) {
                            fileName = cursor.getString(nameIndex)
                        }
                    }
                } catch (_: Exception) {}

                viewModel.addLocalAudio(uri, fileName, 0L)
            }
            Toast.makeText(context, "Imported ${uris.size} audio file(s)", Toast.LENGTH_SHORT).show()
        }
    }

    // Storage permission launcher for scanning device MP3s
    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanLocalMedia()
            Toast.makeText(context, "Scanning local MP3 library...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Storage permission required to scan device MP3s", Toast.LENGTH_SHORT).show()
        }
    }

    // Background Image Picker Launcher (From [05] More menu)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            viewModel.setCustomBackground(uri)
            Toast.makeText(context, "Background image updated", Toast.LENGTH_SHORT).show()
        }
    }

    ElyzarethTheme(themeChoice = themeChoice, darkTheme = isDarkMode) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF07030A)),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (isFullscreen) Modifier else Modifier.systemBarsPadding()),
                contentAlignment = Alignment.Center
            ) {
                // Fixed geometry calculation for 9:16 portrait vs 16:9 landscape
                val frameModifier = when (aspectRatio) {
                    AspectRatioMode.RATIO_9_16 -> {
                        Modifier
                            .fillMaxHeight()
                            .aspectRatio(9f / 16f, matchHeightConstraintsFirst = true)
                    }
                    AspectRatioMode.RATIO_16_9 -> {
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    }
                }

                // Luxurious 3D Glossy Ornate Frame (Fixed geometry, non-reflowing)
                OrnatePlayerFrame(modifier = frameModifier) {
                    // Background layer: Artwork Canvas
                    OrnateArtworkCanvas(
                        song = currentSong,
                        fitMode = fitMode,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Foreground layout
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 6.dp, bottom = 4.dp)
                    ) {
                        // Top Header Controls: [01] Playlist mode, [03] Search, [04] Favorite, [05] More
                        TopControlsBar(
                            song = currentSong,
                            isPlaylistVisible = isPlaylistVisible,
                            isFavorite = currentSong?.isFavorite ?: false,
                            showAuditLabels = playerSettings.showAuditLabels,
                            cleanRecordingMode = playerSettings.cleanRecordingMode,
                            onTogglePlaylistMode = { viewModel.togglePlaylistMode() },
                            onOpenSearch = { viewModel.isSearchOpen.value = true },
                            onToggleFavorite = { viewModel.toggleFavoriteCurrentSong() },
                            onOpenMoreMenu = { viewModel.isMoreMenuOpen.value = true }
                        )

                        // Central Area: Centered Lyrics View & Audit Settings Rail
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            LyricsView(
                                lyrics = currentSong?.lyrics ?: emptyList(),
                                song = currentSong,
                                currentPositionMs = currentPositionMs,
                                settings = playerSettings,
                                onSeekTo = { viewModel.seekTo(it) },
                                onAddSongs = {
                                    audioPickerLauncher.launch(arrayOf("audio/*"))
                                },
                                onLoadDemoSong = {
                                    viewModel.loadDemoSong()
                                },
                                onOpenLyricsEditor = {
                                    viewModel.isLyricsEditorOpen.value = true
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            // Quick Settings Rail ([06], [09], [10], [11], [12], [13], [14], [15])
                            // Displayed in Audit mode so all controls remain testable,
                            // while normal player screen remains clean and cinematic!
                            if (playerSettings.showAuditLabels && !playerSettings.cleanRecordingMode) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxHeight()
                                ) {
                                    SettingsRail(
                                        isDarkMode = isDarkMode,
                                        isFullscreen = isFullscreen,
                                        aspectRatio = aspectRatio,
                                        showAuditLabels = playerSettings.showAuditLabels,
                                        cleanRecordingMode = playerSettings.cleanRecordingMode,
                                        onOpenThemeMenu = { viewModel.isThemeMenuOpen.value = true },
                                        onOpenDspMenu = { viewModel.isDspMenuOpen.value = true },
                                        onOpenSettings = { viewModel.isSettingsOpen.value = true },
                                        onSetLightTheme = { viewModel.setLightTheme() },
                                        onSetDarkTheme = { viewModel.setDarkTheme() },
                                        onCycleFitLayout = { viewModel.cycleFitLayout() },
                                        onToggleFullscreen = { viewModel.toggleFullscreen() },
                                        onToggleAspectRatio = { viewModel.toggleAspectRatio() }
                                    )
                                }
                            }
                        }

                        // Fixed Audio Playback Section: [16] to [23]
                        PlaybackControls(
                            song = currentSong,
                            isPlaying = isPlaying,
                            currentPositionMs = currentPositionMs,
                            durationMs = durationMs,
                            isShuffle = isShuffle,
                            repeatMode = repeatMode,
                            isFavorite = currentSong?.isFavorite ?: false, // Exactly shared with [04]
                            showAuditLabels = playerSettings.showAuditLabels && !playerSettings.cleanRecordingMode,
                            onPlayPause = { viewModel.togglePlayPause() },
                            onSeekTo = { viewModel.seekTo(it) },
                            onNext = { viewModel.playNextSong() },
                            onPrev = { viewModel.playPreviousSong() },
                            onToggleShuffle = { viewModel.toggleShuffle() },
                            onToggleRepeat = { viewModel.toggleRepeat() },
                            onToggleFavorite = { viewModel.toggleFavoriteCurrentSong() },
                            onOpenQueue = { viewModel.isQueueOpen.value = true }
                        )

                        // Bottom Playlist Section: [24] to [37]
                        if (isPlaylistVisible && !isSingleSongMode && !playerSettings.cleanRecordingMode) {
                            PlaylistSection(
                                playlist = playlist,
                                currentSong = currentSong,
                                activeTab = activeTab,
                                isSingleSongMode = isSingleSongMode,
                                isAutoNext = isAutoNext,
                                showAuditLabels = playerSettings.showAuditLabels,
                                cleanRecordingMode = playerSettings.cleanRecordingMode,
                                onTabSelected = { viewModel.setActiveTab(it) },
                                onSongSelected = { viewModel.selectSong(it) },
                                onAddSongs = {
                                    audioPickerLauncher.launch(arrayOf("audio/*"))
                                },
                                onToggleSingleSongMode = { viewModel.toggleSingleSongMode() },
                                onToggleAutoNext = { viewModel.toggleAutoNext() },
                                onOpenLyricsEditor = { viewModel.isLyricsEditorOpen.value = true }
                            )
                        }
                    }
                }

                // Floating Exit Fullscreen pill when in Fullscreen mode
                if (isFullscreen) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xD9160A22))
                            .border(1.dp, ElyzarethGold, RoundedCornerShape(20.dp))
                            .clickable { viewModel.exitFullscreen() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Exit Fullscreen",
                                tint = ElyzarethGoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Exit Fullscreen",
                                color = ElyzarethGoldBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Non-reflowing Overlays
            ThemeMenuOverlay(
                isOpen = isThemeMenuOpen,
                currentTheme = themeChoice,
                onSelectTheme = { viewModel.setTheme(it) },
                onDismiss = { viewModel.isThemeMenuOpen.value = false }
            )

            DspEqOverlay(
                isOpen = isDspMenuOpen,
                dsp = dspSettings,
                isEqSupported = isEqSupported,
                onUpdateDsp = { viewModel.updateDsp(it) },
                onDismiss = { viewModel.isDspMenuOpen.value = false }
            )

            SettingsOverlay(
                isOpen = isSettingsOpen,
                settings = playerSettings,
                onUpdateSettings = { viewModel.updatePlayerSettings(it) },
                onOpenAuditInspector = { viewModel.isAuditInspectorOpen.value = true },
                onDismiss = { viewModel.isSettingsOpen.value = false }
            )

            SearchOverlay(
                isOpen = isSearchOpen,
                allSongs = playlist,
                onSelectSong = { viewModel.selectSong(it) },
                onDismiss = { viewModel.isSearchOpen.value = false }
            )

            MoreSongMenuOverlay(
                isOpen = isMoreMenuOpen,
                song = currentSong,
                onAddSongs = { audioPickerLauncher.launch(arrayOf("audio/*")) },
                onLoadDemoSong = { viewModel.loadDemoSong() },
                onPickBackgroundImage = { imagePickerLauncher.launch("image/*") },
                onEditLyrics = { viewModel.isLyricsEditorOpen.value = true },
                onOpenQueue = { viewModel.isQueueOpen.value = true },
                onOpenSearch = { viewModel.isSearchOpen.value = true },
                onScanDeviceMusic = { storagePermissionLauncher.launch(storagePermission) },
                onOpenDspMenu = { viewModel.isDspMenuOpen.value = true },
                onOpenThemeMenu = { viewModel.isThemeMenuOpen.value = true },
                onOpenSettings = { viewModel.isSettingsOpen.value = true },
                onToggleFullscreen = { viewModel.toggleFullscreen() },
                onToggleAspectRatio = { viewModel.toggleAspectRatio() },
                onRemoveCurrentSong = { viewModel.removeCurrentSong() },
                onClearPlaylist = { viewModel.clearPlaylist() },
                onDismiss = { viewModel.isMoreMenuOpen.value = false }
            )

            QueueOverlay(
                isOpen = isQueueOpen,
                playlist = playlist,
                currentSong = currentSong,
                onSelectSong = { viewModel.selectSong(it) },
                onDismiss = { viewModel.isQueueOpen.value = false }
            )

            LyricsTimingEditorOverlay(
                isOpen = isLyricsEditorOpen,
                song = currentSong,
                currentPlaybackPosMs = currentPositionMs,
                onSaveLyrics = { viewModel.saveSongLyrics(it) },
                onDismiss = { viewModel.isLyricsEditorOpen.value = false }
            )

            ControlAuditInspectorOverlay(
                isOpen = isAuditInspectorOpen,
                onDismiss = { viewModel.isAuditInspectorOpen.value = false }
            )
        }
    }
}
