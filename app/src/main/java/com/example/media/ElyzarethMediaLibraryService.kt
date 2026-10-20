package com.example.media

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.MainActivity
import com.example.audio.DemoAudioGenerator
import com.example.audio.ElyzarethPlaybackEngine
import com.example.model.DefaultSongLibrary
import com.example.model.Song
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class ElyzarethMediaLibraryService : MediaLibraryService() {

    private var mediaLibrarySession: MediaLibrarySession? = null
    private lateinit var playbackEngine: ElyzarethPlaybackEngine
    private lateinit var mediaScanner: LocalMediaScanner

    companion object {
        const val MEDIA_ROOT_ID = "elyzareth_root"
        const val CATEGORY_SONGS = "category_songs"
        const val CATEGORY_ARTISTS = "category_artists"
        const val CATEGORY_ALBUMS = "category_albums"
        const val CATEGORY_PLAYLISTS = "category_playlists"

        const val PREFIX_ARTIST = "artist_"
        const val PREFIX_ALBUM = "album_"
        const val PREFIX_PLAYLIST = "playlist_"
    }

    override fun onCreate() {
        super.onCreate()
        playbackEngine = ElyzarethPlaybackEngine.getInstance(applicationContext)
        mediaScanner = LocalMediaScanner(applicationContext)

        val sessionActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val callback = ElyzarethLibrarySessionCallback()
        mediaLibrarySession = MediaLibrarySession.Builder(this, playbackEngine.player, callback)
            .setSessionActivity(sessionActivityPendingIntent)
            .setId("ElyzarethMediaSession")
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }

    private inner class ElyzarethLibrarySessionCallback : MediaLibrarySession.Callback {

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = MediaItem.Builder()
                .setMediaId(MEDIA_ROOT_ID)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("ELYZARETH")
                        .setArtist("ELYZARETH MUSIC")
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                        .build()
                )
                .build()
            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val songs = getCurrentOrScannedSongs()

            val items: List<MediaItem> = when (parentId) {
                MEDIA_ROOT_ID -> {
                    listOf(
                        createCategoryFolder(CATEGORY_SONGS, "Songs", "All ${songs.size} tracks", MediaMetadata.MEDIA_TYPE_FOLDER_MIXED),
                        createCategoryFolder(CATEGORY_ARTISTS, "Artists", "Browse by artist", MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS),
                        createCategoryFolder(CATEGORY_ALBUMS, "Albums", "Browse by album", MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS),
                        createCategoryFolder(CATEGORY_PLAYLISTS, "Playlists", "Favorites & collections", MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS)
                    )
                }

                CATEGORY_SONGS -> {
                    songs.map { ensurePlayableSong(it).toMediaItem() }
                }

                CATEGORY_ARTISTS -> {
                    val byArtist = LocalMediaScanner.groupByArtists(songs)
                    byArtist.map { (artist, trackList) ->
                        createFolderItem(
                            id = "$PREFIX_ARTIST$artist",
                            title = artist,
                            subtitle = "${trackList.size} songs",
                            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS
                        )
                    }
                }

                CATEGORY_ALBUMS -> {
                    val byAlbum = LocalMediaScanner.groupByAlbums(songs)
                    byAlbum.map { (album, trackList) ->
                        createFolderItem(
                            id = "$PREFIX_ALBUM$album",
                            title = album,
                            subtitle = "${trackList.firstOrNull()?.artist ?: "Unknown Artist"} • ${trackList.size} songs",
                            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS,
                            artworkUri = trackList.firstOrNull()?.artworkUri
                        )
                    }
                }

                CATEGORY_PLAYLISTS -> {
                    val playlists = LocalMediaScanner.getPlaylists(songs)
                    playlists.map { (name, trackList) ->
                        createFolderItem(
                            id = "$PREFIX_PLAYLIST$name",
                            title = name,
                            subtitle = "${trackList.size} tracks",
                            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
                        )
                    }
                }

                else -> {
                    when {
                        parentId.startsWith(PREFIX_ARTIST) -> {
                            val artist = parentId.removePrefix(PREFIX_ARTIST)
                            songs.filter { it.artist.equals(artist, ignoreCase = true) }.map { ensurePlayableSong(it).toMediaItem() }
                        }
                        parentId.startsWith(PREFIX_ALBUM) -> {
                            val album = parentId.removePrefix(PREFIX_ALBUM)
                            songs.filter { it.album.equals(album, ignoreCase = true) }.map { ensurePlayableSong(it).toMediaItem() }
                        }
                        parentId.startsWith(PREFIX_PLAYLIST) -> {
                            val playlistName = parentId.removePrefix(PREFIX_PLAYLIST)
                            val playlists = LocalMediaScanner.getPlaylists(songs)
                            playlists[playlistName]?.map { ensurePlayableSong(it).toMediaItem() } ?: emptyList()
                        }
                        else -> emptyList()
                    }
                }
            }

            return Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(items), params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val songs = getCurrentOrScannedSongs()
            val song = songs.find { it.id == mediaId }
            return if (song != null) {
                Futures.immediateFuture(LibraryResult.ofItem(ensurePlayableSong(song).toMediaItem(), null))
            } else {
                Futures.immediateFuture(LibraryResult.ofError(SessionResult.RESULT_ERROR_BAD_VALUE))
            }
        }

        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
            startIndex: Int,
            startPositionMs: Long
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            val songs = getCurrentOrScannedSongs()
            val resolvedItems = mutableListOf<MediaItem>()
            for (requested in mediaItems) {
                val song = songs.find { it.id == requested.mediaId }
                if (song != null) {
                    resolvedItems.add(ensurePlayableSong(song).toMediaItem())
                } else {
                    resolvedItems.add(requested)
                }
            }
            return Futures.immediateFuture(
                MediaSession.MediaItemsWithStartPosition(
                    resolvedItems,
                    startIndex.coerceIn(0, (resolvedItems.size - 1).coerceAtLeast(0)),
                    startPositionMs.coerceAtLeast(0L)
                )
            )
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            val songs = getCurrentOrScannedSongs()
            val resolvedItems = mutableListOf<MediaItem>()
            for (requested in mediaItems) {
                val song = songs.find { it.id == requested.mediaId }
                if (song != null) {
                    resolvedItems.add(ensurePlayableSong(song).toMediaItem())
                } else {
                    resolvedItems.add(requested)
                }
            }
            return Futures.immediateFuture(resolvedItems)
        }

        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            val songs = getCurrentOrScannedSongs()
            val current = playbackEngine.currentSong.value ?: songs.firstOrNull() ?: DefaultSongLibrary.demoSong
            val targetIdx = songs.indexOfFirst { it.id == current.id }.coerceAtLeast(0)
            val startPos = playbackEngine.currentPositionMs.value

            val items = ImmutableList.copyOf(songs.map { ensurePlayableSong(it).toMediaItem() })
            val result = MediaSession.MediaItemsWithStartPosition(items, targetIdx, startPos)
            return Futures.immediateFuture(result)
        }

        override fun onSearch(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            val songs = getCurrentOrScannedSongs()
            val matchCount = songs.count {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
            session.notifySearchResultChanged(browser, query, matchCount, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetSearchResult(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val songs = getCurrentOrScannedSongs()
            val matches = songs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }.map { ensurePlayableSong(it).toMediaItem() }

            return Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(matches), params))
        }
    }

    private fun ensurePlayableSong(song: Song): Song {
        if (song.fileUri != null) return song
        val demoUri = DemoAudioGenerator.getDemoAudioUri(applicationContext, song.id, song.durationMs)
        return song.copy(fileUri = demoUri)
    }

    private fun getCurrentOrScannedSongs(): List<Song> {
        val inMemory = playbackEngine.playlist.value
        return if (inMemory.isNotEmpty()) {
            inMemory
        } else {
            val scanned = mediaScanner.scanDeviceAudio()
            if (scanned.isNotEmpty()) {
                playbackEngine.setPlaylist(scanned)
                scanned
            } else {
                DefaultSongLibrary.demoList
            }
        }
    }

    private fun createCategoryFolder(
        id: String,
        title: String,
        subtitle: String,
        folderType: Int
    ): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setMediaType(folderType)
            .build()
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)
            .build()
    }

    private fun createFolderItem(
        id: String,
        title: String,
        subtitle: String,
        mediaType: Int,
        artworkUri: android.net.Uri? = null
    ): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setArtworkUri(artworkUri)
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setMediaType(mediaType)
            .build()
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)
            .build()
    }
}
