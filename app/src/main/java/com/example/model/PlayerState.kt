package com.example.model

enum class AspectRatioMode(val label: String, val ratio: Float) {
    RATIO_9_16("9:16 Portrait", 9f / 16f),
    RATIO_16_9("16:9 Landscape", 16f / 9f)
}

enum class ThemeChoice(val displayName: String) {
    ROYAL_GOLD("Royal Gold"),
    ROSE_QUARTZ("Rose Quartz"),
    VELVET_NIGHT("Velvet Night"),
    EMERALD_ORNATE("Emerald"),
    CELESTIAL_PEARL("Celestial Pearl")
}

enum class FitLayoutMode(val displayName: String) {
    COVER("Cover (Fill Frame)"),
    CONTAIN("Contain (Letterbox)"),
    CENTER_CROP("Center Crop"),
    STRETCH("Stretch to Fit")
}

enum class ActiveTab(val label: String) {
    PLAYLIST("Playlist"),
    LYRICS("Lyrics & Timing"),
    LIBRARY("Library")
}

enum class RepeatMode(val label: String) {
    OFF("Repeat Off"),
    ONE("Repeat Current Track"),
    ALL("Repeat All Playlist")
}

data class DspSettings(
    val isEnabled: Boolean = true,
    val band60Hz: Float = 0f,   // dB -10 to +10
    val band230Hz: Float = 0f,  // dB -10 to +10
    val band910Hz: Float = 0f,  // dB -10 to +10
    val band3kHz: Float = 0f,   // dB -10 to +10
    val band14kHz: Float = 0f   // dB -10 to +10
)

data class PlayerSettings(
    val autoScrollLyrics: Boolean = true,
    val showAuditLabels: Boolean = false,
    val cleanRecordingMode: Boolean = false,
    val autoNextEnabled: Boolean = true
)
