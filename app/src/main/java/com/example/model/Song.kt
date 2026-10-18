package com.example.model

import android.net.Uri

data class LyricLine(
    val id: Int,
    val timeMs: Long,
    val text: String
)

data class Song(
    val id: String,
    val title: String,
    val artist: String = "Local Artist",
    val durationMs: Long = 0L,
    val lyrics: List<LyricLine> = emptyList(),
    val isFavorite: Boolean = false,
    val fileUri: Uri? = null,
    val backgroundUri: Uri? = null
)
