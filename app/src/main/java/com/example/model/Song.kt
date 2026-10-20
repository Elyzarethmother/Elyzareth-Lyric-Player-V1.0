package com.example.model

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class LyricLine(
    val id: Int,
    val timeMs: Long,
    val text: String,
    val section: String? = null
)

data class Song(
    val id: String,
    val title: String,
    val artist: String = "Local Artist",
    val album: String = "ELYZARETH Music",
    val durationMs: Long = 0L,
    val lyrics: List<LyricLine> = emptyList(),
    val isFavorite: Boolean = false,
    val fileUri: Uri? = null,
    val backgroundUri: Uri? = null,
    val artworkUri: Uri? = null,
    val albumId: Long? = null
) {
    fun toMediaItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(artworkUri ?: backgroundUri)
            .setIsPlayable(true)
            .setIsBrowsable(false)
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
            .setExtras(Bundle().apply {
                putString("song_id", id)
                putLong("duration_ms", durationMs)
                putBoolean("is_favorite", isFavorite)
            })
            .build()

        // Ensure MediaItem always has a valid URI so ExoPlayer's DefaultMediaSourceFactory can build a MediaSource
        val playUri = fileUri ?: Uri.parse("https://elyzareth.internal/audio/$id.mp3")

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(playUri)
            .setMediaMetadata(metadata)
            .build()
    }
}
