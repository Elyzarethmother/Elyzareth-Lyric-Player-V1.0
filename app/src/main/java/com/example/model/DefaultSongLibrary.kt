package com.example.model

object DefaultSongLibrary {
    // Rich demo song for testing audio playback, karaoke glow, and line synchronization
    val demoSong: Song = Song(
        id = "demo_ballad_1",
        title = "Dosti Se Mohabbat Tak",
        artist = "ELYZARETH Demo Ballad",
        durationMs = 90000L,
        lyrics = listOf(
            LyricLine(1, 0L, "यह कहानी है दो दिलों की..."),
            LyricLine(2, 5000L, "जहाँ दोस्ती से शुरू हुआ एक खूबसूरत सफ़र"),
            LyricLine(3, 10000L, "हर शाम एक हँसी, हर सुबह एक नई बात"),
            LyricLine(4, 15000L, "तेरी मुस्कान में छिपा था मेरा सारा जहाँ"),
            LyricLine(5, 20000L, "हवाओं में बहती हुई सी तेरी खुशबू"),
            LyricLine(6, 25000L, "कभी खामोशी में भी समझ लेते थे एक-दूजे को"),
            LyricLine(7, 30000L, "फिर एक दिन दिल ने धीरे से कहा..."),
            LyricLine(8, 36000L, "यह सिर्फ दोस्ती नहीं, मोहब्बत है..."),
            LyricLine(9, 42000L, "तेरी आँखों में शाम ढले, तेरी बाहों में सवेरा"),
            LyricLine(10, 48000L, "हर लम्हा तेरे साथ, जैसे कोई हसीन सपना"),
            LyricLine(11, 55000L, "हाथ थामे चलना इस अनजाने रास्ते पर"),
            LyricLine(12, 62000L, "सच्ची मोहब्बत हमेशा साथ निभाती है"),
            LyricLine(13, 70000L, "वही पुरानी दोस्ती, अब हमेशा के लिए प्यार...")
        ),
        isFavorite = false,
        fileUri = null
    )

    val songs: List<Song> = emptyList()
}
