package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.model.FitLayoutMode
import com.example.model.Song

@Composable
fun OrnateArtworkCanvas(
    song: Song?,
    fitMode: FitLayoutMode,
    modifier: Modifier = Modifier
) {
    val contentScale = when (fitMode) {
        FitLayoutMode.COVER -> ContentScale.Crop
        FitLayoutMode.CONTAIN -> ContentScale.Fit
        FitLayoutMode.CENTER_CROP -> ContentScale.Crop
        FitLayoutMode.STRETCH -> ContentScale.FillBounds
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0612))
    ) {
        // User imported custom background artwork (if available)
        if (song?.backgroundUri != null) {
            AsyncImage(
                model = song.backgroundUri,
                contentDescription = "Custom Artist Background",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Luxurious dark vignette overlay ensuring 100% lyric contrast
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Base ambient gradient if no custom background
            if (song?.backgroundUri == null) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E0C2B),
                            Color(0xFF12071A),
                            Color(0xFF0A030E)
                        ),
                        startY = 0f,
                        endY = h
                    )
                )

                // Subtle luxury watermark filigree in background
                val goldAccent = Color(0x1AFFD700)
                drawCircle(
                    color = goldAccent,
                    radius = w * 0.35f,
                    center = Offset(w * 0.5f, h * 0.35f),
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = Color(0x10FFD700),
                    radius = w * 0.48f,
                    center = Offset(w * 0.5f, h * 0.35f),
                    style = Stroke(width = 1.0f)
                )
            }

            // Dark readability scrim overlay
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xB3000000),
                        Color(0x8C000000),
                        Color(0xE6000000)
                    )
                )
            )

            // Ornate Corner Filigree Accents (Non-interactive frame styling)
            val filigreeColor = Color(0x73FFD700)
            val stroke = Stroke(width = 1.8f)
            val cornerSize = 48f
            val m = 14f

            // Top-left
            drawPath(
                Path().apply {
                    moveTo(m, m + cornerSize)
                    lineTo(m, m)
                    lineTo(m + cornerSize, m)
                    moveTo(m + 8f, m + 8f)
                    lineTo(m + cornerSize * 0.6f, m + 8f)
                    moveTo(m + 8f, m + 8f)
                    lineTo(m + 8f, m + cornerSize * 0.6f)
                },
                color = filigreeColor,
                style = stroke
            )
            // Top-right
            drawPath(
                Path().apply {
                    moveTo(w - m - cornerSize, m)
                    lineTo(w - m, m)
                    lineTo(w - m, m + cornerSize)
                    moveTo(w - m - 8f, m + 8f)
                    lineTo(w - m - cornerSize * 0.6f, m + 8f)
                    moveTo(w - m - 8f, m + 8f)
                    lineTo(w - m - 8f, m + cornerSize * 0.6f)
                },
                color = filigreeColor,
                style = stroke
            )
            // Bottom-left
            drawPath(
                Path().apply {
                    moveTo(m, h - m - cornerSize)
                    lineTo(m, h - m)
                    lineTo(m + cornerSize, h - m)
                },
                color = filigreeColor,
                style = stroke
            )
            // Bottom-right
            drawPath(
                Path().apply {
                    moveTo(w - m - cornerSize, h - m)
                    lineTo(w - m, h - m)
                    lineTo(w - m, h - m - cornerSize)
                },
                color = filigreeColor,
                style = stroke
            )
        }
    }
}
