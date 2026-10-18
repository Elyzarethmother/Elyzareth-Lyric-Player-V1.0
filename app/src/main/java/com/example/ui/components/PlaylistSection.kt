package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveTab
import com.example.model.Song
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright
import kotlinx.coroutines.launch

@Composable
fun PlaylistSection(
    playlist: List<Song>,
    currentSong: Song?,
    activeTab: ActiveTab,
    isSingleSongMode: Boolean,
    isAutoNext: Boolean,
    showAuditLabels: Boolean,
    cleanRecordingMode: Boolean,
    onTabSelected: (ActiveTab) -> Unit,
    onSongSelected: (Song) -> Unit,
    onAddSongs: () -> Unit,
    onToggleSingleSongMode: () -> Unit,
    onToggleAutoNext: () -> Unit,
    onOpenLyricsEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSingleSongMode || cleanRecordingMode) return

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xE612081C))
            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(top = 4.dp, bottom = 6.dp)
    ) {
        // Upper row: [24] Playlist tab, [25] Lyrics tab, [26] Library tab, [27] Add Songs, [28] Single Song, [29] Auto Next
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // [24] Playlist Tab
                TabButton(
                    id = "24",
                    label = "Playlist",
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    isSelected = activeTab == ActiveTab.PLAYLIST,
                    showAuditLabels = showAuditLabels,
                    onClick = { onTabSelected(ActiveTab.PLAYLIST) }
                )

                // [25] Lyrics Tab
                TabButton(
                    id = "25",
                    label = "Lyrics",
                    icon = Icons.Default.EditNote,
                    isSelected = activeTab == ActiveTab.LYRICS,
                    showAuditLabels = showAuditLabels,
                    onClick = {
                        onTabSelected(ActiveTab.LYRICS)
                        onOpenLyricsEditor()
                    }
                )

                // [26] Library Tab
                TabButton(
                    id = "26",
                    label = "Library",
                    icon = Icons.Default.LibraryMusic,
                    isSelected = activeTab == ActiveTab.LIBRARY,
                    showAuditLabels = showAuditLabels,
                    onClick = { onTabSelected(ActiveTab.LIBRARY) }
                )
            }

            // Quick Actions: [27] Add Songs, [28] Single Song Mode, [29] Auto Next
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // [27] Add Songs
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuditBadge(id = "27", visible = showAuditLabels)
                    IconButton(
                        onClick = onAddSongs,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFD700))
                            .testTag("btn_add_songs")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Songs",
                            tint = ElyzarethGoldBright,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // [28] Single Song Mode
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuditBadge(id = "28", visible = showAuditLabels)
                    IconButton(
                        onClick = onToggleSingleSongMode,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("btn_single_song_mode")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Single Song Mode",
                            tint = if (isSingleSongMode) ElyzarethGoldBright else Color(0x99FFFFFF),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // [29] Auto Next
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuditBadge(id = "29", visible = showAuditLabels)
                    IconButton(
                        onClick = onToggleAutoNext,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("btn_auto_next")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Auto Next",
                            tint = if (isAutoNext) ElyzarethGoldBright else Color(0x66FFFFFF),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Carousel Controls: [30] Previous item, Track Cards 31-36, [37] Next item
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [30] Previous playlist carousel
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                AuditBadge(id = "30", visible = showAuditLabels)
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val target = (scrollState.value - 200).coerceAtLeast(0)
                            scrollState.animateScrollTo(target)
                        }
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .testTag("btn_prev_playlist_carousel")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous playlist carousel",
                        tint = ElyzarethGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Horizontal Track Cards: 31 to 36
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playlist.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Playlist empty. Tap [27] Add Songs to import audio files.",
                            color = Color(0x88FFFFFF),
                            fontSize = 11.5.sp
                        )
                    }
                } else {
                    val cardSlots = listOf("31", "32", "33", "34", "35", "36")
                    val songsToShow = playlist.take(6)

                    songsToShow.forEachIndexed { index, song ->
                        val auditId = cardSlots.getOrElse(index) { "36" }
                        val isSelected = song.id == currentSong?.id

                        TrackCardItem(
                            auditId = auditId,
                            song = song,
                            isSelected = isSelected,
                            showAuditLabels = showAuditLabels,
                            onClick = { onSongSelected(song) }
                        )
                    }
                }
            }

            // [37] Next playlist carousel
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                AuditBadge(id = "37", visible = showAuditLabels)
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val target = (scrollState.value + 200).coerceAtMost(scrollState.maxValue)
                            scrollState.animateScrollTo(target)
                        }
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .testTag("btn_next_playlist_carousel")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next playlist carousel",
                        tint = ElyzarethGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    id: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    showAuditLabels: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0x40E5B869) else Color(0x1AFFFFFF))
            .border(
                1.dp,
                if (isSelected) ElyzarethGoldBright else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 7.dp, vertical = 4.dp)
            .testTag("tab_$label")
    ) {
        AuditBadge(id = id, visible = showAuditLabels)
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ElyzarethGoldBright else Color(0xCCFFFFFF),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = if (isSelected) ElyzarethGoldBright else Color(0xCCFFFFFF),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun TrackCardItem(
    auditId: String,
    song: Song,
    isSelected: Boolean,
    showAuditLabels: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(116.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("track_card_$auditId"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2E1736) else Color(0xFF1B0E23)
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ElyzarethGoldBright) else androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x44FFD700))
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuditBadge(id = auditId, visible = showAuditLabels)
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Playing",
                        tint = ElyzarethGoldBright,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = song.title,
                color = if (isSelected) ElyzarethGoldBright else Color.White,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artist,
                color = Color(0x99FFFFFF),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
