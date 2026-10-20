package com.example

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.media3.common.MediaMetadata
import androidx.test.core.app.ApplicationProvider
import com.example.audio.ElyzarethPlaybackEngine
import com.example.media.ElyzarethMediaLibraryService
import com.example.media.LocalMediaScanner
import com.example.model.DefaultSongLibrary
import com.example.model.RepeatMode
import com.example.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AndroidAutoMediaTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun verifyAndroidAutoManifestDeclarations() {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SERVICES or PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
        )

        // Verify required permissions for Android Auto background playback and local audio access
        val permissions = packageInfo.requestedPermissions?.toSet() ?: emptySet()
        assertTrue("Manifest must declare FOREGROUND_SERVICE", permissions.contains("android.permission.FOREGROUND_SERVICE"))
        assertTrue("Manifest must declare FOREGROUND_SERVICE_MEDIA_PLAYBACK", permissions.contains("android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK"))
        assertTrue("Manifest must declare READ_MEDIA_AUDIO", permissions.contains("android.permission.READ_MEDIA_AUDIO"))
        assertTrue("Manifest must declare WAKE_LOCK", permissions.contains("android.permission.WAKE_LOCK"))

        // Verify MediaLibraryService is registered
        val services = packageInfo.services ?: emptyArray()
        val mediaService = services.find { it.name == ElyzarethMediaLibraryService::class.java.name }
        assertNotNull("ElyzarethMediaLibraryService must be declared in AndroidManifest", mediaService)
        assertTrue("ElyzarethMediaLibraryService must be exported for Android Auto host", mediaService!!.exported)

        // Verify automotive app descriptor metadata exists in application
        val appInfo = packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val carAppRes = appInfo.metaData?.getInt("com.google.android.gms.car.application") ?: 0
        assertTrue("com.google.android.gms.car.application meta-data must be configured", carAppRes != 0)
    }

    @Test
    fun verifySongToMediaItemConversion() {
        val song = Song(
            id = "test_song_1",
            title = "Golden Sunset",
            artist = "ELYZARETH Symphony",
            album = "Royal Acoustics",
            durationMs = 180000L,
            isFavorite = true
        )

        val mediaItem = song.toMediaItem()
        assertEquals("test_song_1", mediaItem.mediaId)
        val metadata = mediaItem.mediaMetadata
        assertEquals("Golden Sunset", metadata.title.toString())
        assertEquals("ELYZARETH Symphony", metadata.artist.toString())
        assertEquals("Royal Acoustics", metadata.albumTitle.toString())
        assertTrue("MediaItem must be playable", metadata.isPlayable == true)
        assertFalse("Song MediaItem must not be browsable", metadata.isBrowsable == true)
        assertEquals(MediaMetadata.MEDIA_TYPE_MUSIC, metadata.mediaType)
    }

    @Test
    fun verifyMediaBrowsingGroupingHierarchy() {
        val testSongs = listOf(
            Song(id = "s1", title = "Track 1", artist = "Artist Alpha", album = "Album One", isFavorite = true),
            Song(id = "s2", title = "Track 2", artist = "Artist Alpha", album = "Album Two", isFavorite = false),
            Song(id = "s3", title = "Track 3", artist = "Artist Beta", album = "Album One", isFavorite = true)
        )

        val byArtists = LocalMediaScanner.groupByArtists(testSongs)
        assertEquals("Must group into 2 distinct artists", 2, byArtists.size)
        assertEquals(2, byArtists["Artist Alpha"]?.size)
        assertEquals(1, byArtists["Artist Beta"]?.size)

        val byAlbums = LocalMediaScanner.groupByAlbums(testSongs)
        assertEquals("Must group into 2 distinct albums", 2, byAlbums.size)
        assertEquals(2, byAlbums["Album One"]?.size)
        assertEquals(1, byAlbums["Album Two"]?.size)

        val playlists = LocalMediaScanner.getPlaylists(testSongs)
        assertTrue("Must include Current Queue", playlists.containsKey("Current Queue"))
        assertTrue("Must include Favorites", playlists.containsKey("Favorites"))
        assertEquals("Favorites playlist must contain 2 items", 2, playlists["Favorites"]?.size)
    }

    @Test
    fun verifySharedPlaybackEngineControls() {
        val engine = ElyzarethPlaybackEngine.getInstance(context)
        assertNotNull("Playback engine must be instantiated", engine)

        val testSongs = DefaultSongLibrary.demoList
        engine.setPlaylist(testSongs)
        assertEquals(testSongs.size, engine.playlist.value.size)

        // Select song
        val target = testSongs[1]
        engine.selectSong(target, playWhenReady = false)
        assertEquals(target.id, engine.currentSong.value?.id)

        // Seek
        engine.seekTo(10000L)
        assertEquals(10000L, engine.currentPositionMs.value)

        // Repeat mode
        engine.setRepeatMode(RepeatMode.ALL)
        assertEquals(RepeatMode.ALL, engine.repeatMode.value)

        // Shuffle
        engine.setShuffle(true)
        assertTrue(engine.isShuffle.value)
    }
}
