package com.example.data

import android.content.Context
import android.net.Uri
import com.example.model.AspectRatioMode
import com.example.model.LyricLine
import com.example.model.Song
import com.example.model.ThemeChoice
import org.json.JSONArray
import org.json.JSONObject

class ProjectStorage(context: Context) {
    private val prefs = context.getSharedPreferences("elyzareth_player_prefs", Context.MODE_PRIVATE)

    fun savePlaylist(songs: List<Song>) {
        val root = JSONArray()
        for (song in songs) {
            val obj = JSONObject().apply {
                put("id", song.id)
                put("title", song.title)
                put("artist", song.artist)
                put("durationMs", song.durationMs)
                put("isFavorite", song.isFavorite)
                put("fileUri", song.fileUri?.toString() ?: "")
                put("backgroundUri", song.backgroundUri?.toString() ?: "")

                val lyricsArr = JSONArray()
                for (line in song.lyrics) {
                    val lObj = JSONObject().apply {
                        put("id", line.id)
                        put("timeMs", line.timeMs)
                        put("text", line.text)
                    }
                    lyricsArr.put(lObj)
                }
                put("lyrics", lyricsArr)
            }
            root.put(obj)
        }
        prefs.edit().putString("saved_playlist", root.toString()).apply()
    }

    fun loadPlaylist(): List<Song> {
        val jsonStr = prefs.getString("saved_playlist", null) ?: return emptyList()
        val list = mutableListOf<Song>()
        try {
            val root = JSONArray(jsonStr)
            for (i in 0 until root.length()) {
                val obj = root.getJSONObject(i)
                val id = obj.optString("id", "song_$i")
                val title = obj.optString("title", "Imported Track")
                val artist = obj.optString("artist", "Local Artist")
                val durationMs = obj.optLong("durationMs", 0L)
                val isFavorite = obj.optBoolean("isFavorite", false)
                val uriStr = obj.optString("fileUri", "")
                val bgUriStr = obj.optString("backgroundUri", "")

                val lyrics = mutableListOf<LyricLine>()
                val lyricsArr = obj.optJSONArray("lyrics")
                if (lyricsArr != null) {
                    for (j in 0 until lyricsArr.length()) {
                        val lObj = lyricsArr.getJSONObject(j)
                        lyrics.add(
                            LyricLine(
                                id = lObj.optInt("id", j + 1),
                                timeMs = lObj.optLong("timeMs", 0L),
                                text = lObj.optString("text", "")
                            )
                        )
                    }
                }

                list.add(
                    Song(
                        id = id,
                        title = title,
                        artist = artist,
                        durationMs = durationMs,
                        lyrics = lyrics,
                        isFavorite = isFavorite,
                        fileUri = if (uriStr.isNotBlank()) Uri.parse(uriStr) else null,
                        backgroundUri = if (bgUriStr.isNotBlank()) Uri.parse(bgUriStr) else null
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun savePreferences(
        theme: ThemeChoice,
        isDark: Boolean,
        aspectRatio: AspectRatioMode,
        autoNext: Boolean
    ) {
        prefs.edit()
            .putString("pref_theme", theme.name)
            .putBoolean("pref_is_dark", isDark)
            .putString("pref_aspect_ratio", aspectRatio.name)
            .putBoolean("pref_auto_next", autoNext)
            .apply()
    }

    fun getSavedTheme(): ThemeChoice {
        val name = prefs.getString("pref_theme", ThemeChoice.ROYAL_GOLD.name)
        return try { ThemeChoice.valueOf(name!!) } catch (_: Exception) { ThemeChoice.ROYAL_GOLD }
    }

    fun getSavedIsDark(): Boolean = prefs.getBoolean("pref_is_dark", true)

    fun getSavedAspectRatio(): AspectRatioMode {
        val name = prefs.getString("pref_aspect_ratio", AspectRatioMode.RATIO_9_16.name)
        return try { AspectRatioMode.valueOf(name!!) } catch (_: Exception) { AspectRatioMode.RATIO_9_16 }
    }

    fun getSavedAutoNext(): Boolean = prefs.getBoolean("pref_auto_next", true)
}
