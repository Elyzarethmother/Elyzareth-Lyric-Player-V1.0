package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Song
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright

/**
 * Clean top bar for the main player screen.
 * Per visual redesign specification:
 * - No search button or back button at the top.
 * - The only top-right control is the three-dot menu.
 * - Song title is placed near the bottom above the seek bar.
 */
@Composable
fun TopControlsBar(
    song: Song? = null,
    isPlaylistVisible: Boolean = false,
    isFavorite: Boolean = false,
    showAuditLabels: Boolean = false,
    cleanRecordingMode: Boolean = false,
    onTogglePlaylistMode: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onOpenMoreMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Only top-right control: Three-dot Settings Menu
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuditBadge(id = "05", visible = showAuditLabels && !cleanRecordingMode)
            IconButton(
                onClick = onOpenMoreMenu,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_more")
                    .testTag("three_dot_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu & Settings",
                    tint = ElyzarethGoldBright,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
