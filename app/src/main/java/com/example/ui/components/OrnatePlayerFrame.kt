package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElyzarethGold
import com.example.ui.theme.ElyzarethGoldBright

/**
 * Premium ornate 3D gold frame around the vertical player.
 * Features "ELYZARETH" embossed into the top frame and "ELYZARETH MUSIC" integrated
 * vertically into the frame's left side trim.
 */
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
            .padding(3.5.dp) // Bevel depth gap
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
                shape = RoundedCornerShape(cornerRadius - 3.dp)
            )
            .clip(RoundedCornerShape(cornerRadius - 3.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Integrated Frame Bar: "✦ ELYZARETH ✦"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF38230B),
                                Color(0xFF1B0E05)
                            )
                        )
                    )
                    .border(
                        width = 0.8.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0x33FFD700),
                                ElyzarethGoldBright,
                                Color(0x33FFD700)
                            )
                        ),
                        shape = RectangleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✦  E L Y Z A R E T H  ✦",
                    color = ElyzarethGoldBright,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 3.5.sp
                )
            }

            // 2. Middle area: Left spine with "ELYZARETH MUSIC" + Main Canvas
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Integrated left vertical gold spine: "ELYZARETH MUSIC"
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(18.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF2E1B09),
                                    Color(0xFF1B0E05)
                                )
                            )
                        )
                        .border(
                            width = 0.6.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0x44FFD700),
                                    Color(0x11FFD700),
                                    Color(0x44FFD700)
                                )
                            ),
                            shape = RectangleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E L Y Z A R E T H   M U S I C",
                        color = ElyzarethGold,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 2.sp,
                        maxLines = 1,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = -90f
                        }
                    )
                }

                // Main Interior Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    content()

                    // 3D Glass Gloss Sheen across upper portion of player
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Glossy diagonal highlight curve
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0x18FFFFFF),
                                    Color(0x06FFFFFF),
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
    }
}
