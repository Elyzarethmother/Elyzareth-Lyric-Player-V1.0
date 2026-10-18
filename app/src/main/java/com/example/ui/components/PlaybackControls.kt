package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RepeatMode
import com.example.model.Song
import com.example.ui.theme.ElyzarethCrimson
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright
import com.example.ui.theme.FavoritePink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackControls(
    song: Song?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    isFavorite: Boolean, // Shares exact state with control 04
    showAuditLabels: Boolean,
    onPlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragSliderValue by remember { mutableFloatStateOf(0f) }

    val effectiveDuration = if (durationMs > 0) durationMs else (song?.durationMs ?: 0L)
    val progress = if (effectiveDuration > 0) {
        if (isDraggingSlider) dragSliderValue else (currentPositionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        // [16] Seek Bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuditBadge(id = "16", visible = showAuditLabels)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatTime(if (isDraggingSlider) (dragSliderValue * effectiveDuration).toLong() else currentPositionMs),
                        color = ElyzarethGoldBright,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = formatTime(effectiveDuration),
                    color = Color(0xB3FFFFFF),
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Slider(
                value = progress,
                onValueChange = {
                    isDraggingSlider = true
                    dragSliderValue = it
                },
                onValueChangeFinished = {
                    isDraggingSlider = false
                    onSeekTo((dragSliderValue * effectiveDuration).toLong())
                },
                enabled = song != null && effectiveDuration > 0,
                colors = SliderDefaults.colors(
                    thumbColor = ElyzarethGoldBright,
                    activeTrackColor = ElyzarethGold,
                    inactiveTrackColor = Color(0x33FFFFFF)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .testTag("seek_bar_slider")
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Playback Action Buttons: [17] Favorite, [18] Shuffle, [19] Prev, [20] Play/Pause, [21] Next, [22] Repeat, [23] Queue
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [17] Playback Favorite (Shared state with 04)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuditBadge(id = "17", visible = showAuditLabels)
                IconButton(
                    onClick = onToggleFavorite,
                    enabled = song != null,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("btn_playback_favorite")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Playback Favorite",
                        tint = if (isFavorite) FavoritePink else Color(0xCCFFFFFF),
                        modifier = Modifier.size(21.dp)
                    )
                }
            }

            // [18] Shuffle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuditBadge(id = "18", visible = showAuditLabels)
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("btn_shuffle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) ElyzarethGoldBright else Color(0x66FFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // [19] Previous track
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuditBadge(id = "19", visible = showAuditLabels)
                IconButton(
                    onClick = onPrev,
                    enabled = song != null,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("btn_prev")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = ElyzarethGold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // [20] Play / Pause (Hero 3D Gold Action Button)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuditBadge(id = "20", visible = showAuditLabels)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(elevation = 12.dp, shape = CircleShape, spotColor = ElyzarethGoldBright)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFEC8B), ElyzarethGold, ElyzarethCrimson)
                            )
                        )
                        .clickable(enabled = song != null) { onPlayPause() }
                        .testTag("btn_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF160A18),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // [21] Next track
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuditBadge(id = "21", visible = showAuditLabels)
                IconButton(
                    onClick = onNext,
                    enabled = song != null,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("btn_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = ElyzarethGold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // [22] Repeat
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuditBadge(id = "22", visible = showAuditLabels)
                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("btn_repeat")
                ) {
                    Icon(
                        imageVector = when (repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (repeatMode != RepeatMode.OFF) ElyzarethGoldBright else Color(0x66FFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // [23] Queue
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AuditBadge(id = "23", visible = showAuditLabels)
                IconButton(
                    onClick = onOpenQueue,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("btn_queue")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = "Queue",
                        tint = Color(0xCCFFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format("%02d:%02d", minutes, seconds)
}
