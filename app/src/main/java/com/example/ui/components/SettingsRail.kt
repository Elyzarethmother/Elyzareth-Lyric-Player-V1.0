package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AspectRatioMode
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright

@Composable
fun SettingsRail(
    isDarkMode: Boolean,
    isFullscreen: Boolean,
    aspectRatio: AspectRatioMode,
    showAuditLabels: Boolean,
    cleanRecordingMode: Boolean,
    onOpenThemeMenu: () -> Unit,
    onOpenDspMenu: () -> Unit,
    onOpenSettings: () -> Unit,
    onSetLightTheme: () -> Unit,
    onSetDarkTheme: () -> Unit,
    onCycleFitLayout: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleAspectRatio: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (cleanRecordingMode) return

    Column(
        modifier = modifier
            .width(52.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(Color(0xD9140A1F))
            .border(1.dp, Color(0x44FFD700), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .padding(vertical = 10.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // [06] Theme Menu (Icon-only when closed!)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "06", visible = showAuditLabels)
            IconButton(
                onClick = onOpenThemeMenu,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_theme_menu")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme Menu",
                    tint = ElyzarethGold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // [09] DSP + EQ Menu (Icon-only when closed!)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "09", visible = showAuditLabels)
            IconButton(
                onClick = onOpenDspMenu,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_dsp_menu")
            ) {
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = "DSP and EQ Menu",
                    tint = ElyzarethGold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // [10] Settings
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "10", visible = showAuditLabels)
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xCCFFFFFF),
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        // [11] Light Theme
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "11", visible = showAuditLabels)
            IconButton(
                onClick = onSetLightTheme,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (!isDarkMode) Color(0x44FFD700) else Color.Transparent)
                    .testTag("btn_light_theme")
            ) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Light Theme",
                    tint = if (!isDarkMode) ElyzarethGoldBright else Color(0x88FFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // [12] Dark Theme
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "12", visible = showAuditLabels)
            IconButton(
                onClick = onSetDarkTheme,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) Color(0x44FFD700) else Color.Transparent)
                    .testTag("btn_dark_theme")
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Dark Theme",
                    tint = if (isDarkMode) ElyzarethGoldBright else Color(0x88FFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // [13] Fit/layout (Cycle Cover/Contain/Center-Crop/Stretch)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "13", visible = showAuditLabels)
            IconButton(
                onClick = onCycleFitLayout,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_fit_layout")
            ) {
                Icon(
                    imageVector = Icons.Default.FitScreen,
                    contentDescription = "Fit/Layout",
                    tint = Color(0xCCFFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // [14] Fullscreen
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "14", visible = showAuditLabels)
            IconButton(
                onClick = onToggleFullscreen,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_fullscreen")
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "Fullscreen",
                    tint = ElyzarethGold,
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        // [15] Recording aspect ratio (9:16 or 16:9)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AuditBadge(id = "15", visible = showAuditLabels)
            IconButton(
                onClick = onToggleAspectRatio,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (aspectRatio == AspectRatioMode.RATIO_16_9) Color(0x55E5B869) else Color.Transparent)
                    .testTag("btn_aspect_ratio")
            ) {
                Icon(
                    imageVector = Icons.Default.AspectRatio,
                    contentDescription = "Recording Aspect Ratio",
                    tint = if (aspectRatio == AspectRatioMode.RATIO_16_9) ElyzarethGoldBright else Color(0xCCFFFFFF),
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}
