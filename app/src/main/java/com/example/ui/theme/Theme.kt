package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.ThemeChoice

data class ElyzarethVisualTheme(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onBackground: Color,
    val borderGold: Color,
    val accentGlow: Color
)

fun getThemePalette(choice: ThemeChoice, isDark: Boolean): ElyzarethVisualTheme {
    return when (choice) {
        ThemeChoice.ROYAL_GOLD -> {
            if (isDark) {
                ElyzarethVisualTheme(
                    primary = ElyzarethGold,
                    secondary = ElyzarethRose,
                    background = Color(0xFF0F0814),
                    surface = Color(0xFF1B1124),
                    surfaceVariant = Color(0xFF281836),
                    onSurface = TextPrimaryDark,
                    onBackground = TextPrimaryDark,
                    borderGold = Color(0xFFD4AF37),
                    accentGlow = Color(0xFFFFD700)
                )
            } else {
                ElyzarethVisualTheme(
                    primary = Color(0xFF9E782F),
                    secondary = Color(0xFFC04E78),
                    background = Color(0xFFFDF7F0),
                    surface = Color(0xFFF5EBE0),
                    surfaceVariant = Color(0xFFEADBCE),
                    onSurface = TextPrimaryLight,
                    onBackground = TextPrimaryLight,
                    borderGold = Color(0xFFB8860B),
                    accentGlow = Color(0xFFD4AF37)
                )
            }
        }
        ThemeChoice.ROSE_QUARTZ -> {
            if (isDark) {
                ElyzarethVisualTheme(
                    primary = Color(0xFFF4A6B8),
                    secondary = Color(0xFFFFC0CB),
                    background = Color(0xFF180A14),
                    surface = Color(0xFF2A1022),
                    surfaceVariant = Color(0xFF3B1831),
                    onSurface = Color(0xFFFFEBF2),
                    onBackground = Color(0xFFFFEBF2),
                    borderGold = Color(0xFFE898AC),
                    accentGlow = Color(0xFFFF8DA1)
                )
            } else {
                ElyzarethVisualTheme(
                    primary = Color(0xFFC44D6C),
                    secondary = Color(0xFFD97790),
                    background = Color(0xFFFFF5F7),
                    surface = Color(0xFFFCE4EB),
                    surfaceVariant = Color(0xFFF8D1DC),
                    onSurface = Color(0xFF330D1B),
                    onBackground = Color(0xFF330D1B),
                    borderGold = Color(0xFFD97790),
                    accentGlow = Color(0xFFE85D83)
                )
            }
        }
        ThemeChoice.VELVET_NIGHT -> {
            if (isDark) {
                ElyzarethVisualTheme(
                    primary = Color(0xFFBA8BFF),
                    secondary = Color(0xFF7E57C2),
                    background = Color(0xFF090614),
                    surface = Color(0xFF150F28),
                    surfaceVariant = Color(0xFF231A3E),
                    onSurface = Color(0xFFF3EAFF),
                    onBackground = Color(0xFFF3EAFF),
                    borderGold = Color(0xFF9D65D8),
                    accentGlow = Color(0xFFBA8BFF)
                )
            } else {
                ElyzarethVisualTheme(
                    primary = Color(0xFF6A3DA3),
                    secondary = Color(0xFF8E5ACD),
                    background = Color(0xFFF7F3FF),
                    surface = Color(0xFFEDE4FD),
                    surfaceVariant = Color(0xFFDFD1F7),
                    onSurface = Color(0xFF1D0D38),
                    onBackground = Color(0xFF1D0D38),
                    borderGold = Color(0xFF8E5ACD),
                    accentGlow = Color(0xFF6A3DA3)
                )
            }
        }
        ThemeChoice.EMERALD_ORNATE -> {
            if (isDark) {
                ElyzarethVisualTheme(
                    primary = Color(0xFF48C78E),
                    secondary = Color(0xFFD4AF37),
                    background = Color(0xFF05140D),
                    surface = Color(0xFF0A2218),
                    surfaceVariant = Color(0xFF133628),
                    onSurface = Color(0xFFE7F9F0),
                    onBackground = Color(0xFFE7F9F0),
                    borderGold = Color(0xFF3CA875),
                    accentGlow = Color(0xFF48C78E)
                )
            } else {
                ElyzarethVisualTheme(
                    primary = Color(0xFF1F7A52),
                    secondary = Color(0xFF9E782F),
                    background = Color(0xFFF2FAF6),
                    surface = Color(0xFFE1F3EA),
                    surfaceVariant = Color(0xFFCCEBDD),
                    onSurface = Color(0xFF0B2B1D),
                    onBackground = Color(0xFF0B2B1D),
                    borderGold = Color(0xFF2E9465),
                    accentGlow = Color(0xFF1F7A52)
                )
            }
        }
        ThemeChoice.CELESTIAL_PEARL -> {
            if (isDark) {
                ElyzarethVisualTheme(
                    primary = Color(0xFF90CAF9),
                    secondary = Color(0xFFB0BEC5),
                    background = Color(0xFF0A1118),
                    surface = Color(0xFF131E2A),
                    surfaceVariant = Color(0xFF1E2E40),
                    onSurface = Color(0xFFE8F1F8),
                    onBackground = Color(0xFFE8F1F8),
                    borderGold = Color(0xFF64B5F6),
                    accentGlow = Color(0xFF90CAF9)
                )
            } else {
                ElyzarethVisualTheme(
                    primary = Color(0xFF1E6091),
                    secondary = Color(0xFF52796F),
                    background = Color(0xFFF3F8FC),
                    surface = Color(0xFFE4F0F8),
                    surfaceVariant = Color(0xFFD1E4F2),
                    onSurface = Color(0xFF0A1C2A),
                    onBackground = Color(0xFF0A1C2A),
                    borderGold = Color(0xFF2E86AB),
                    accentGlow = Color(0xFF1E6091)
                )
            }
        }
    }
}

@Composable
fun ElyzarethTheme(
    themeChoice: ThemeChoice = ThemeChoice.ROYAL_GOLD,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val palette = getThemePalette(themeChoice, darkTheme)
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.surfaceVariant,
            onSurface = palette.onSurface,
            onBackground = palette.onBackground
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.surfaceVariant,
            onSurface = palette.onSurface,
            onBackground = palette.onBackground
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
