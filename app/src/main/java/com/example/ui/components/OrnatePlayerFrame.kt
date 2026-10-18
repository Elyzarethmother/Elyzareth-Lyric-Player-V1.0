package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElyzarethCrimson
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright

@Composable
fun OrnatePlayerFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val cornerRadius = 24.dp

    Box(
        modifier = modifier
            // Outer deep shadow for 3D elevation
            .shadow(elevation = 28.dp, shape = RoundedCornerShape(cornerRadius), spotColor = Color(0xFFFFD700))
            .clip(RoundedCornerShape(cornerRadius))
            // Layered 3D Metallic Beveled Outer Edge
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF6B4E1B),
                        Color(0xFF2E1B09),
                        Color(0xFF4A3414)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFFFEC9E),
                        ElyzarethGoldBright,
                        Color(0xFF8B5E14),
                        Color(0xFFFFE57F)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(4.5.dp) // Bevel depth gap
            // Inner raised frame
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0x99FFD700),
                        Color(0x22FFD700),
                        Color(0x88D4AF37)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius - 4.dp)
            )
            .clip(RoundedCornerShape(cornerRadius - 4.dp))
    ) {
        // Main Interior Canvas
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            // 3D Glass Gloss Sheen across upper portion of player
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Glossy diagonal highlight curve
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x1FFFFFFF),
                            Color(0x0AFFFFFF),
                            Color(0x00000000)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w * 0.7f, h * 0.45f)
                    )
                )
            }
        }
    }
}
