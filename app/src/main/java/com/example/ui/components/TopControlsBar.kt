package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright
import com.example.ui.theme.FavoritePink

@Composable
fun TopControlsBar(
    song: Song?,
    isPlaylistVisible: Boolean,
    isFavorite: Boolean,
    showAuditLabels: Boolean,
    cleanRecordingMode: Boolean,
    onTogglePlaylistMode: () -> Unit,
    onOpenSearch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenMoreMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ELYZARETH Luxury Embossed Brand Plate
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Text(
                text = "✦ E L Y Z A R E T H ✦",
                color = ElyzarethGoldBright,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                letterSpacing = 2.5.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Group: [01] Playlist Mode toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                AuditBadge(id = "01", visible = showAuditLabels && !cleanRecordingMode)
                Spacer(modifier = Modifier.width(3.dp))
                IconButton(
                    onClick = onTogglePlaylistMode,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isPlaylistVisible) Color(0x55E5B869) else Color(0x33000000))
                        .testTag("btn_playlist_mode")
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewCarousel,
                        contentDescription = "Playlist Mode",
                        tint = if (isPlaylistVisible) ElyzarethGoldBright else Color(0xCCFFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Center: Song Title & Artist (Truncated/wrapped to prevent layout deformation)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = song?.title ?: "No Song Loaded",
                    color = if (song != null) ElyzarethGoldBright else Color(0x88FFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                if (song != null) {
                    Text(
                        text = song.artist,
                        color = Color(0xB3FFFFFF),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Right Group: [03] Search, [04] Favorite, [05] More
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // [03] Search
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuditBadge(id = "03", visible = showAuditLabels && !cleanRecordingMode)
                    IconButton(
                        onClick = onOpenSearch,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("btn_search")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xCCFFFFFF),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                // [04] Top Favorite
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuditBadge(id = "04", visible = showAuditLabels && !cleanRecordingMode)
                    IconButton(
                        onClick = onToggleFavorite,
                        enabled = song != null,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("btn_top_favorite")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) FavoritePink else Color(0x88FFFFFF),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                // [05] More
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuditBadge(id = "05", visible = showAuditLabels && !cleanRecordingMode)
                    IconButton(
                        onClick = onOpenMoreMenu,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("btn_more")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = ElyzarethGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
