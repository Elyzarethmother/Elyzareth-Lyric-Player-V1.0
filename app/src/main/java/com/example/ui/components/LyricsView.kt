package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LyricLine
import com.example.model.PlayerSettings
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.model.Song

@Composable
fun LyricsView(
    lyrics: List<LyricLine>,
    song: Song? = null,
    currentPositionMs: Long,
    settings: PlayerSettings,
    onSeekTo: (Long) -> Unit,
    onAddSongs: () -> Unit = {},
    onLoadDemoSong: () -> Unit = {},
    onOpenLyricsEditor: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val activeIndex by remember(lyrics, currentPositionMs) {
        derivedStateOf {
            val idx = lyrics.indexOfLast { it.timeMs <= currentPositionMs }
            if (idx == -1 && lyrics.isNotEmpty()) 0 else idx
        }
    }

    LaunchedEffect(activeIndex, settings.autoScrollLyrics) {
        if (settings.autoScrollLyrics && activeIndex in lyrics.indices) {
            coroutineScope.launch {
                val targetIndex = (activeIndex - 1).coerceAtLeast(0)
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        if (lyrics.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (song == null) {
                        Text(
                            text = "No Song Loaded",
                            color = ElyzarethGoldBright,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Import local audio files (.mp3, .wav) or load the reference demo ballad with synchronized lyrics.",
                            color = Color(0xB3FFFFFF),
                            textAlign = TextAlign.Center,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = onAddSongs,
                            colors = ButtonDefaults.buttonColors(containerColor = ElyzarethGold),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF160A18), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Audio Files [27]", color = Color(0xFF160A18), fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }

                        Button(
                            onClick = onLoadDemoSong,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFD700)),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElyzarethGoldBright, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Load Demo Ballad & Lyrics", color = ElyzarethGoldBright, fontSize = 12.sp)
                        }
                    } else {
                        Text(
                            text = song.title,
                            color = ElyzarethGoldBright,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "No lyrics for this song yet.\nPaste lyrics or sync timestamps in the editor.",
                            color = Color(0xB3FFFFFF),
                            textAlign = TextAlign.Center,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = onOpenLyricsEditor,
                            colors = ButtonDefaults.buttonColors(containerColor = ElyzarethGold),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF160A18), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Paste & Sync Lyrics [25]", color = Color(0xFF160A18), fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("lyrics_list"),
                contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeIndex
                    val isPast = index < activeIndex

                    val textColor by animateColorAsState(
                        targetValue = when {
                            isActive -> ElyzarethGoldBright
                            isPast -> Color(0xB3FFFFFF)
                            else -> Color(0x66FFFFFF)
                        },
                        animationSpec = tween(durationMillis = 250),
                        label = "LyricColor"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSeekTo(line.timeMs) }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = line.text,
                            color = textColor,
                            fontSize = if (isActive) 19.sp else 16.sp,
                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            lineHeight = if (isActive) 26.sp else 22.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Navigation Controls: [07] Previous lyric page & [08] Next lyric page
        if (!settings.cleanRecordingMode && lyrics.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // [07] Previous Lyric Navigation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            val newIdx = (listState.firstVisibleItemIndex - 3).coerceAtLeast(0)
                            listState.animateScrollToItem(newIdx)
                        }
                    }
                ) {
                    AuditBadge(id = "07", visible = settings.showAuditLabels)
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x99180C24))
                            .testTag("prev_lyric_page"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Previous Lyric Navigation",
                            tint = ElyzarethGoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // [08] Next Lyric Navigation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            val newIdx = (listState.firstVisibleItemIndex + 3).coerceAtMost(
                                (lyrics.size - 1).coerceAtLeast(0)
                            )
                            listState.animateScrollToItem(newIdx)
                        }
                    }
                ) {
                    AuditBadge(id = "08", visible = settings.showAuditLabels)
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x99180C24))
                            .testTag("next_lyric_page"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Next Lyric Navigation",
                            tint = ElyzarethGoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
