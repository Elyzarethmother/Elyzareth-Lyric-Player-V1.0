package com.example

import com.example.model.ControlAuditRegister
import com.example.model.DefaultSongLibrary
import com.example.model.LyricLine
import com.example.model.Song
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun verifyControlAuditRegisterCoverage() {
        val entries = ControlAuditRegister.entries
        // Verify all 37 numbered controls are in the register
        val idSet = entries.map { it.id }.toSet()
        for (i in 1..37) {
            val idStr = String.format("%02d", i)
            assertTrue("Register must contain ID $idStr", idSet.contains(idStr))
        }

        // Verify ID 02 is strictly reserved with no invented feature
        val entry02 = entries.first { it.id == "02" }
        assertTrue("Control 02 must be reserved", entry02.isReserved)
        assertFalse("Control 02 must not be approved as a control", entry02.isApproved)

        // Verify all other 36 controls are explicitly approved
        val approved = entries.filter { it.isApproved }
        assertEquals("36 controls must be approved", 36, approved.size)
    }

    @Test
    fun verifyDefaultPlaylistStartsEmpty() {
        // Must start with an empty playlist; no hardcoded songs per specification
        val songs = DefaultSongLibrary.songs
        assertTrue("Default playlist must be empty until user imports local files", songs.isEmpty())
    }

    @Test
    fun verifySongAndLyricModels() {
        val song = Song(
            id = "test_1",
            title = "My Imported Track",
            artist = "Local Artist",
            durationMs = 120_000L,
            lyrics = listOf(
                LyricLine(1, 0L, "First line of user lyrics"),
                LyricLine(2, 5_000L, "Second line of user lyrics")
            ),
            isFavorite = true
        )
        assertEquals("My Imported Track", song.title)
        assertEquals(2, song.lyrics.size)
        assertTrue(song.isFavorite)
    }
}
