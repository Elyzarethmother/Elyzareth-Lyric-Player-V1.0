package com.example.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.model.DefaultSongLibrary
import com.example.model.Song

class LocalMediaScanner(private val context: Context) {

    fun scanDeviceAudio(): List<Song> {
        val songList = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.ALBUM_ID
            } else {
                MediaStore.Audio.Media.ALBUM_ID
            }
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Track $id"
                    val artist = cursor.getString(artistColumn).let {
                        if (it == null || it == "<unknown>") "Unknown Artist" else it
                    }
                    val album = cursor.getString(albumColumn).let {
                        if (it == null || it == "<unknown>") "Unknown Album" else it
                    }
                    val durationMs = cursor.getLong(durationColumn)
                    val albumId = cursor.getLong(albumIdColumn)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val artworkUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    songList.add(
                        Song(
                            id = "device_audio_$id",
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = durationMs,
                            lyrics = emptyList(),
                            isFavorite = false,
                            fileUri = contentUri,
                            artworkUri = artworkUri,
                            albumId = albumId
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("LocalMediaScanner", "Could not query MediaStore: ${e.message}")
        }

        // If no device songs were found (e.g., emulator or permissions pending), include demo songs
        return if (songList.isEmpty()) {
            DefaultSongLibrary.demoList
        } else {
            songList
        }
    }

    companion object {
        fun groupByArtists(songs: List<Song>): Map<String, List<Song>> {
            return songs.groupBy { it.artist.ifBlank { "Unknown Artist" } }
        }

        fun groupByAlbums(songs: List<Song>): Map<String, List<Song>> {
            return songs.groupBy { it.album.ifBlank { "Unknown Album" } }
        }

        fun getPlaylists(songs: List<Song>): Map<String, List<Song>> {
            val map = mutableMapOf<String, List<Song>>()
            map["Current Queue"] = songs
            val favorites = songs.filter { it.isFavorite }
            if (favorites.isNotEmpty()) {
                map["Favorites"] = favorites
            }
            map["ELYZARETH Demo Ballads"] = DefaultSongLibrary.demoList
            return map
        }
    }
}
