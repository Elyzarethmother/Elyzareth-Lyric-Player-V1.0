package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LyricLine
import com.example.model.PlayerSettings
import com.example.model.Song
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright
import kotlinx.coroutines.launch

/**
 * Helper to determine section name (e.g. "Verse 1", "Chorus", "Bridge")
 * based on line metadata or natural stanza grouping.
 */
private fun resolveSectionName(line: LyricLine, index: Int, totalLines: Int): String {
    if (!line.section.isNullOrBlank()) return line.section

    val text = line.text.trim()
    if (text.startsWith("[") && text.contains("]")) {
        val candidate = text.substringAfter("[").substringBefore("]").trim()
        if (candidate.isNotBlank()) return candidate
    }

    // Default stanza progression if untagged
    val stanza = (index / 4) + 1
    return when {
        stanza == 1 -> "Verse 1"
        stanza == 2 -> "Chorus"
        stanza == 3 -> "Verse 2"
        stanza == 4 -> "Chorus"
        stanza == 5 -> "Bridge"
        else -> "Chorus"
    }
}

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

    val activeSectionName by remember(lyrics, activeIndex) {
        derivedStateOf {
            if (lyrics.isNotEmpty() && activeIndex in lyrics.indices) {
                resolveSectionName(lyrics[activeIndex], activeIndex, lyrics.size)
            } else {
                "Lyrics"
            }
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
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        if (lyrics.isEmpty()) {
            // Elegant Centered Empty / Onboarding State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xCC14091E),
                    border = BorderStroke(1.dp, Color(0x44FFD700)),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .shadow(16.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0xFFFFD700))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFD700)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ElyzarethGoldBright,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        if (song == null) {
                            Text(
                                text = "ELYZARETH PLAYER",
                                color = ElyzarethGoldBright,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Import your audio files (.mp3, .wav, .m4a) or explore the pre-loaded demo ballad with synchronized lyrics.",
                                color = Color(0xB3FFFFFF),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = onAddSongs,
                                colors = ButtonDefaults.buttonColors(containerColor = ElyzarethGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF160A18), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Audio Files", color = Color(0xFF160A18), fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            }

                            Button(
                                onClick = onLoadDemoSong,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFD700)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0x66FFD700)),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElyzarethGoldBright, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Load Demo Ballad & Lyrics", color = ElyzarethGoldBright, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                                text = "No synchronized lyrics for this song yet.\nPaste lyrics or sync timestamps in the editor.",
                                color = Color(0xB3FFFFFF),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = onOpenLyricsEditor,
                                colors = ButtonDefaults.buttonColors(containerColor = ElyzarethGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFF160A18), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Paste & Sync Lyrics", color = Color(0xFF160A18), fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Main Dynamic Lyrics Display
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section Indicator Label (e.g. "✦ VERSE 1 ✦", "✦ CHORUS ✦")
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x66160A22),
                    border = BorderStroke(1.dp, Color(0x55FFD700)),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "✦  ${activeSectionName.uppercase()}  ✦",
                        color = ElyzarethGoldBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                // Centered translucent card over background artwork
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0x550C0414),
                    border = BorderStroke(0.75.dp, Color(0x2BFFD700)),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("lyrics_list"),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp, start = 12.dp, end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                    fontSize = if (isActive) 19.5.sp else 15.5.sp,
                                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontFamily = FontFamily.Serif,
                                    textAlign = TextAlign.Center,
                                    lineHeight = if (isActive) 27.sp else 22.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        // Lyric Page Stepper Buttons: only show if explicitly enabled in Audit Mode
        if (settings.showAuditLabels && lyrics.isNotEmpty()) {
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
                    AuditBadge(id = "07", visible = true)
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x99180C24))
                            .testTag("prev_lyric_page"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Previous Lyric Navigation",
                            tint = ElyzarethGoldBright,
                            modifier = Modifier.size(18.dp)
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
                    AuditBadge(id = "08", visible = true)
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x99180C24))
                            .testTag("next_lyric_page"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Next Lyric Navigation",
                            tint = ElyzarethGoldBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
