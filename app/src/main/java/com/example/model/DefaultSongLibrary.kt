package com.example.model

object DefaultSongLibrary {
    // Rich demo song for testing audio playback, karaoke glow, and line synchronization
    val demoSong: Song = Song(
        id = "demo_ballad_1",
        title = "Dosti Se Mohabbat Tak",
        artist = "ELYZARETH Demo Ballad",
        album = "ELYZARETH Ballads Vol. 1",
        durationMs = 90000L,
        lyrics = listOf(
            LyricLine(1, 0L, "यह कहानी है दो दिलों की...", "Verse 1"),
            LyricLine(2, 5000L, "जहाँ दोस्ती से शुरू हुआ एक खूबसूरत सफ़र", "Verse 1"),
            LyricLine(3, 10000L, "हर शाम एक हँसी, हर सुबह एक नई बात", "Verse 1"),
            LyricLine(4, 15000L, "तेरी मुस्कान में छिपा था मेरा सारा जहाँ", "Verse 1"),
            LyricLine(5, 20000L, "हवाओं में बहती हुई सी तेरी खुशबू", "Verse 1"),
            LyricLine(6, 25000L, "कभी खामोशी में भी समझ लेते थे एक-दूजे को", "Bridge"),
            LyricLine(7, 30000L, "फिर एक दिन दिल ने धीरे से कहा...", "Bridge"),
            LyricLine(8, 36000L, "यह सिर्फ दोस्ती नहीं, मोहब्बत है...", "Chorus"),
            LyricLine(9, 42000L, "तेरी आँखों में शाम ढले, तेरी बाहों में सवेरा", "Chorus"),
            LyricLine(10, 48000L, "हर लम्हा तेरे साथ, जैसे कोई हसीन सपना", "Chorus"),
            LyricLine(11, 55000L, "हाथ थामे चलना इस अनजाने रास्ते पर", "Verse 2"),
            LyricLine(12, 62000L, "सच्ची मोहब्बत हमेशा साथ निभाती है", "Verse 2"),
            LyricLine(13, 70000L, "वही पुरानी दोस्ती, अब हमेशा के लिए प्यार...", "Chorus")
        ),
        isFavorite = false,
        fileUri = null
    )

    val demoSong2: Song = Song(
        id = "demo_ambient_2",
        title = "Golden Horizon Serenade",
        artist = "ELYZARETH Symphony",
        album = "Royal Acoustics",
        durationMs = 120000L,
        lyrics = listOf(
            LyricLine(1, 0L, "Underneath the gilded skies of dusk...", "Prelude"),
            LyricLine(2, 8000L, "Notes that dance like amber in the air", "Prelude"),
            LyricLine(3, 16000L, "Where the golden horizon softly whispers", "Verse 1"),
            LyricLine(4, 28000L, "A melody that lingers beyond the silent night", "Chorus"),
            LyricLine(5, 45000L, "Pure resonance in every sacred chord", "Outro")
        ),
        isFavorite = true,
        fileUri = null
    )

    val demoList: List<Song> = listOf(demoSong, demoSong2)

    val songs: List<Song> = emptyList()
}
