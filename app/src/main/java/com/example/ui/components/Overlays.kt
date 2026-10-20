package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ControlAuditRegister
import com.example.model.DspSettings
import com.example.model.LyricLine
import com.example.model.PlayerSettings
import com.example.model.Song
import com.example.model.ThemeChoice
import com.example.ui.theme.ElyzarethCrimson
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright

// [06] Theme Menu Overlay
@Composable
fun ThemeMenuOverlay(
    isOpen: Boolean,
    currentTheme: ThemeChoice,
    onSelectTheme: (ThemeChoice) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF5160C22),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElyzarethGold),
            modifier = Modifier
                .width(290.dp)
                .testTag("theme_menu_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuditBadge(id = "06")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Visual Themes",
                            color = ElyzarethGoldBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                ThemeChoice.values().forEach { theme ->
                    val isSelected = theme == currentTheme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0x33FFD700) else Color.Transparent)
                            .clickable {
                                onSelectTheme(theme)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = theme.displayName,
                            color = if (isSelected) ElyzarethGoldBright else Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = ElyzarethGoldBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// [09] DSP + EQ Menu Overlay
@Composable
fun DspEqOverlay(
    isOpen: Boolean,
    dsp: DspSettings,
    isEqSupported: Boolean,
    onUpdateDsp: (DspSettings) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF5160A22),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElyzarethGold),
            modifier = Modifier
                .width(320.dp)
                .testTag("dsp_menu_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuditBadge(id = "09")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Audio Equalizer (DSP)",
                            color = ElyzarethGoldBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Equalizer Enable Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Equalizer Processing", color = Color.White, fontSize = 13.sp)
                        Text(
                            text = if (isEqSupported) "Hardware EQ Active" else "EQ session pending audio stream",
                            color = if (isEqSupported) Color(0xFF81C784) else Color(0xFFFFB74D),
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = dsp.isEnabled,
                        onCheckedChange = { onUpdateDsp(dsp.copy(isEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElyzarethGoldBright,
                            checkedTrackColor = ElyzarethGold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5 Frequency Bands
                val bands = listOf(
                    Triple("60 Hz (Bass)", dsp.band60Hz) { v: Float -> dsp.copy(band60Hz = v) },
                    Triple("230 Hz (Low-Mid)", dsp.band230Hz) { v: Float -> dsp.copy(band230Hz = v) },
                    Triple("910 Hz (Mid)", dsp.band910Hz) { v: Float -> dsp.copy(band910Hz = v) },
                    Triple("3.6 kHz (High-Mid)", dsp.band3kHz) { v: Float -> dsp.copy(band3kHz = v) },
                    Triple("14 kHz (Treble)", dsp.band14kHz) { v: Float -> dsp.copy(band14kHz = v) }
                )

                bands.forEach { (label, value, updateFunc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = label, color = Color(0xCCFFFFFF), fontSize = 11.5.sp)
                        Text(
                            text = "${if (value > 0) "+" else ""}${value.toInt()} dB",
                            color = ElyzarethGoldBright,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = value,
                        onValueChange = { onUpdateDsp(updateFunc(it)) },
                        valueRange = -10f..10f,
                        enabled = dsp.isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = ElyzarethGoldBright,
                            activeTrackColor = ElyzarethGold
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        onUpdateDsp(DspSettings(isEnabled = true))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset EQ to Flat (0 dB)", color = Color.White, fontSize = 11.5.sp)
                }
            }
        }
    }
}

// [10] Settings Overlay
@Composable
fun SettingsOverlay(
    isOpen: Boolean,
    settings: PlayerSettings,
    onUpdateSettings: (PlayerSettings) -> Unit,
    onOpenAuditInspector: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF5160A22),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElyzarethGold),
            modifier = Modifier
                .width(310.dp)
                .testTag("settings_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuditBadge(id = "10")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Player Settings",
                            color = ElyzarethGoldBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Auto Scroll Lyrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Auto-Scroll Lyrics", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = settings.autoScrollLyrics,
                        onCheckedChange = { onUpdateSettings(settings.copy(autoScrollLyrics = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElyzarethGoldBright,
                            checkedTrackColor = ElyzarethGold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Show Audit Badges [01-37]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Audit Labels [01-37]", color = Color.White, fontSize = 13.sp)
                        Text(text = "Visible control numbers", color = Color(0x99FFFFFF), fontSize = 10.sp)
                    }
                    Switch(
                        checked = settings.showAuditLabels,
                        onCheckedChange = { onUpdateSettings(settings.copy(showAuditLabels = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElyzarethGoldBright,
                            checkedTrackColor = ElyzarethGold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Clean Recording Mode (Hide editing & labels)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Clean Recording Mode", color = Color.White, fontSize = 13.sp)
                        Text(text = "For phone screen recording", color = Color(0x99FFFFFF), fontSize = 10.sp)
                    }
                    Switch(
                        checked = settings.cleanRecordingMode,
                        onCheckedChange = { onUpdateSettings(settings.copy(cleanRecordingMode = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElyzarethGoldBright,
                            checkedTrackColor = ElyzarethGold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onOpenAuditInspector()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFD700)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Audit Register Inspector (01–37)", color = ElyzarethGoldBright, fontSize = 11.5.sp)
                }
            }
        }
    }
}

// [03] Search Overlay
@Composable
fun SearchOverlay(
    isOpen: Boolean,
    allSongs: List<Song>,
    onSelectSong: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    BackHandler { onDismiss() }

    var query by remember { mutableStateOf("") }
    val filtered = remember(query, allSongs) {
        if (query.isBlank()) allSongs else allSongs.filter {
            it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF5160A22),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElyzarethGold),
            modifier = Modifier
                .width(320.dp)
                .height(400.dp)
                .testTag("search_dialog")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuditBadge(id = "03")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Search Loaded Songs",
                            color = ElyzarethGoldBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search song title...", color = Color(0x66FFFFFF), fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElyzarethGold,
                        unfocusedBorderColor = Color(0x44FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No matching songs found.", color = Color(0x66FFFFFF), fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filtered) { song ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onSelectSong(song)
                                        onDismiss()
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = song.title, color = ElyzarethGoldBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = song.artist, color = Color(0x99FFFFFF), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSectionHeader(title: String) {
    Text(
        text = title,
        color = ElyzarethGold,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
    )
}

@Composable
private fun MenuItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDestructive) Color(0x22FF5252) else Color(0x22FFD700)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) Color(0xFFFF5252) else ElyzarethGoldBright,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isDestructive) Color(0xFFFF8A80) else Color.White,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = if (isDestructive) Color(0xAAFF8A80) else Color(0x99FFFFFF),
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

// [05] Three-Dot Settings Menu Overlay
@Composable
fun MoreSongMenuOverlay(
    isOpen: Boolean,
    song: Song?,
    onAddSongs: () -> Unit,
    onLoadDemoSong: () -> Unit,
    onPickBackgroundImage: () -> Unit,
    onEditLyrics: () -> Unit,
    onOpenQueue: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onScanDeviceMusic: () -> Unit = {},
    onOpenDspMenu: () -> Unit = {},
    onOpenThemeMenu: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onToggleFullscreen: () -> Unit = {},
    onToggleAspectRatio: () -> Unit = {},
    onRemoveCurrentSong: () -> Unit,
    onClearPlaylist: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xF8150A20),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, ElyzarethGold),
            modifier = Modifier
                .width(330.dp)
                .heightIn(max = 560.dp)
                .testTag("more_menu_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with reliable Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuditBadge(id = "05")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Settings & Menu",
                            color = ElyzarethGoldBright,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_more_menu_btn")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Menu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SECTION 1: AUDIO & LIBRARY
                MenuSectionHeader(title = "AUDIO & LIBRARY")

                MenuItemRow(
                    icon = Icons.Default.Add,
                    title = "Import Audio / Add Songs",
                    subtitle = "Import .mp3, .wav, .m4a files",
                    onClick = {
                        onDismiss()
                        onAddSongs()
                    }
                )

                MenuItemRow(
                    icon = Icons.Default.LibraryMusic,
                    title = "Scan Device MP3s",
                    subtitle = "Sync local storage & Android Auto library",
                    onClick = {
                        onDismiss()
                        onScanDeviceMusic()
                    }
                )

                MenuItemRow(
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    title = "Playlist & Play Queue",
                    subtitle = "View and reorder song library",
                    onClick = {
                        onDismiss()
                        onOpenQueue()
                    }
                )

                MenuItemRow(
                    icon = Icons.Default.Search,
                    title = "Search Songs",
                    subtitle = "Filter by title or artist",
                    onClick = {
                        onDismiss()
                        onOpenSearch()
                    }
                )

                MenuItemRow(
                    icon = Icons.Default.PlayArrow,
                    title = "Load Demo Ballad & Lyrics",
                    subtitle = "Reset to sample track with sync timings",
                    onClick = {
                        onDismiss()
                        onLoadDemoSong()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SECTION 2: LYRICS & TIMING
                MenuSectionHeader(title = "LYRICS & SYNCHRONIZATION")

                MenuItemRow(
                    icon = Icons.Default.EditNote,
                    title = "Paste or Edit Lyrics & Timing",
                    subtitle = "Synchronize lines with audio timestamps",
                    onClick = {
                        onDismiss()
                        onEditLyrics()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SECTION 3: THEME & DISPLAY
                MenuSectionHeader(title = "THEME & ARTWORK")

                MenuItemRow(
                    icon = Icons.Default.Palette,
                    title = "Theme & Accent Colors",
                    subtitle = "Royal Gold, Emerald, Rose Quartz",
                    onClick = {
                        onDismiss()
                        onOpenThemeMenu()
                    }
                )

                MenuItemRow(
                    icon = Icons.Default.Image,
                    title = "Background Artwork",
                    subtitle = "Choose custom photo or artwork",
                    onClick = {
                        onDismiss()
                        onPickBackgroundImage()
                    }
                )

                MenuItemRow(
                    icon = Icons.Default.AspectRatio,
                    title = "Toggle Aspect Ratio",
                    subtitle = "Switch between 9:16 portrait and 16:9 wide",
                    onClick = {
                        onDismiss()
                        onToggleAspectRatio()
                    }
                )

                MenuItemRow(
                    icon = Icons.Default.Fullscreen,
                    title = "Toggle Fullscreen",
                    subtitle = "Hide system status and nav bars",
                    onClick = {
                        onDismiss()
                        onToggleFullscreen()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SECTION 4: SOUND & PREFERENCES
                MenuSectionHeader(title = "SOUND & PREFERENCES")

                MenuItemRow(
                    icon = Icons.Default.Equalizer,
                    title = "Equalizer & Audio DSP",
                    subtitle = "5-band hardware/software tone controls",
                    onClick = {
                        onDismiss()
                        onOpenDspMenu()
                    }
                )

                MenuItemRow(
                    icon = Icons.Default.Settings,
                    title = "Player Preferences",
                    subtitle = "Auto-scroll lyrics, continuous play",
                    onClick = {
                        onDismiss()
                        onOpenSettings()
                    }
                )

                // SECTION 5: MANAGE CURRENT TRACK
                if (song != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MenuSectionHeader(title = "MANAGE TRACK")

                    MenuItemRow(
                        icon = Icons.Default.Delete,
                        title = "Remove Song from Playlist",
                        subtitle = song.title,
                        isDestructive = true,
                        onClick = {
                            onDismiss()
                            onRemoveCurrentSong()
                        }
                    )
                }

                MenuItemRow(
                    icon = Icons.Default.DeleteSweep,
                    title = "Clear Entire Playlist",
                    subtitle = "Remove all tracks",
                    isDestructive = true,
                    onClick = {
                        onDismiss()
                        onClearPlaylist()
                    }
                )
            }
        }
    }
}

// [23] Queue Overlay
@Composable
fun QueueOverlay(
    isOpen: Boolean,
    playlist: List<Song>,
    currentSong: Song?,
    onSelectSong: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF5160A22),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElyzarethGold),
            modifier = Modifier
                .width(320.dp)
                .height(400.dp)
                .testTag("queue_dialog")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuditBadge(id = "23")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Playback Queue (${playlist.size})",
                            color = ElyzarethGoldBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (playlist.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("Queue is empty. Import songs with [27].", color = Color(0x66FFFFFF), fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(playlist) { idx, song ->
                            val isPlayingThis = song.id == currentSong?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isPlayingThis) Color(0x33FFD700) else Color.Transparent)
                                    .clickable {
                                        onSelectSong(song)
                                        onDismiss()
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${idx + 1}.",
                                    color = if (isPlayingThis) ElyzarethGoldBright else Color(0x66FFFFFF),
                                    fontSize = 12.sp,
                                    modifier = Modifier.width(24.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = song.title,
                                        color = if (isPlayingThis) ElyzarethGoldBright else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (isPlayingThis) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(text = song.artist, color = Color(0x99FFFFFF), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// [25] Lyrics Tab & Timing Editor Overlay (Manual lyric-timing editor & paste editor)
@Composable
fun LyricsTimingEditorOverlay(
    isOpen: Boolean,
    song: Song?,
    currentPlaybackPosMs: Long,
    onSaveLyrics: (List<LyricLine>) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen || song == null) return

    BackHandler { onDismiss() }

    var activeTab by remember { mutableIntStateOf(0) } // 0: Timing Editor, 1: Paste Raw Lyrics
    var editableLyrics by remember(song) { mutableStateOf(song.lyrics) }
    var rawText by remember(song) {
        mutableStateOf(song.lyrics.joinToString("\n") { it.text })
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF5160A22),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElyzarethGold),
            modifier = Modifier
                .width(340.dp)
                .height(490.dp)
                .testTag("lyrics_editor_dialog")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuditBadge(id = "25")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lyric & Timing Editor",
                            color = ElyzarethGoldBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Mode Tabs
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color.Transparent,
                    contentColor = ElyzarethGoldBright
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Timing Sync (${editableLyrics.size} lines)", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Paste Lyrics Text", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (activeTab == 0) {
                    // Timing Editor
                    Text(
                        text = "Current playback: ${currentPlaybackPosMs / 1000}s. Tap 'Set Current' to mark line timing on the fly.",
                        color = Color(0x99FFFFFF),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (editableLyrics.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                "No lines yet. Switch to 'Paste Lyrics Text' to add lines.",
                                color = Color(0x66FFFFFF),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            itemsIndexed(editableLyrics) { idx, line ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x22FFFFFF))
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = line.text,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Start: ${line.timeMs / 1000}s",
                                            color = ElyzarethGoldBright,
                                            fontSize = 10.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Set Current button (On the fly sync during playback!)
                                        Button(
                                            onClick = {
                                                editableLyrics = editableLyrics.toMutableList().also {
                                                    it[idx] = line.copy(timeMs = currentPlaybackPosMs)
                                                }
                                            },
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ElyzarethGold),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text("Set Current", fontSize = 9.sp, color = Color(0xFF160A18), fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Nudge -1s
                                        Button(
                                            onClick = {
                                                val newTime = (line.timeMs - 1000L).coerceAtLeast(0L)
                                                editableLyrics = editableLyrics.toMutableList().also {
                                                    it[idx] = line.copy(timeMs = newTime)
                                                }
                                            },
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFD700)),
                                            modifier = Modifier.size(height = 26.dp, width = 30.dp)
                                        ) {
                                            Text("-", fontSize = 11.sp, color = ElyzarethGoldBright)
                                        }

                                        Spacer(modifier = Modifier.width(2.dp))

                                        // Nudge +1s
                                        Button(
                                            onClick = {
                                                val newTime = line.timeMs + 1000L
                                                editableLyrics = editableLyrics.toMutableList().also {
                                                    it[idx] = line.copy(timeMs = newTime)
                                                }
                                            },
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFD700)),
                                            modifier = Modifier.size(height = 26.dp, width = 30.dp)
                                        ) {
                                            Text("+", fontSize = 11.sp, color = ElyzarethGoldBright)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onSaveLyrics(editableLyrics)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElyzarethGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Timings", color = Color(0xFF160A18), fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Paste Raw Lyrics Text
                    Text(
                        text = "Paste full song lyrics below. Each new line will become a separate lyric segment.",
                        color = Color(0x99FFFFFF),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = rawText,
                        onValueChange = { rawText = it },
                        placeholder = { Text("Paste song lyrics here...\nLine 1\nLine 2\n...", color = Color(0x55FFFFFF)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElyzarethGold,
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
                            val newLyrics = lines.mapIndexed { idx, text ->
                                LyricLine(
                                    id = idx + 1,
                                    timeMs = idx * 4000L, // Initial default 4s spacing for adjustment
                                    text = text
                                )
                            }
                            editableLyrics = newLyrics
                            onSaveLyrics(newLyrics)
                            activeTab = 0
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElyzarethGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Convert & Generate Lines", color = Color(0xFF160A18), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Complete Control Audit Inspector Dialog (Audits 01-37 per instruction 11)
@Composable
fun ControlAuditInspectorOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF514081E),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElyzarethGoldBright),
            modifier = Modifier
                .width(340.dp)
                .height(520.dp)
                .testTag("audit_inspector_dialog")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ELYZARETH Control Register",
                            color = ElyzarethGoldBright,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Control Audit (01 to 37)",
                            color = Color(0x99FFFFFF),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ControlAuditRegister.entries) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (entry.isReserved) Color(0x22FF5252) else Color(0x221B1028))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AuditBadge(id = entry.id, visible = true)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = entry.name,
                                        color = if (entry.isReserved) Color(0xFFFF8A80) else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = entry.status,
                                        color = if (entry.isReserved) Color(0xFFFF8A80) else Color(0xFF81C784),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = entry.requiredBehavior,
                                    color = Color(0x99FFFFFF),
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
