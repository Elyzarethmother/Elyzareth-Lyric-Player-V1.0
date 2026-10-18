package com.example.model

/**
 * Register entry definition for the official ELYZARETH numbered control audit.
 */
data class ControlRegisterEntry(
    val id: String,
    val name: String,
    val group: String,
    val requiredBehavior: String,
    val isApproved: Boolean = true,
    val isReserved: Boolean = false,
    val status: String = "Implemented"
)

object ControlAuditRegister {
    val entries: List<ControlRegisterEntry> = listOf(
        ControlRegisterEntry("01", "Playlist Mode", "Main player and lyrics", "Show or hide playlist management view", isApproved = true),
        ControlRegisterEntry("02", "Reserved", "Main player and lyrics", "No control assigned in reference; do not invent one", isApproved = false, isReserved = true, status = "Reserved (Omitted by Spec)"),
        ControlRegisterEntry("03", "Search", "Main player and lyrics", "Search loaded songs by title", isApproved = true),
        ControlRegisterEntry("04", "Favorite", "Main player and lyrics", "Mark or unmark current song as favorite", isApproved = true),
        ControlRegisterEntry("05", "More", "Main player and lyrics", "Open a compact menu of approved song actions", isApproved = true),
        ControlRegisterEntry("06", "Theme menu", "Theme, DSP and display settings", "Open theme choices; apply selected visual theme (icon-only closed)", isApproved = true),
        ControlRegisterEntry("07", "Previous lyric page", "Main player and lyrics", "Scroll to earlier lyrics, if lyrics span pages", isApproved = true),
        ControlRegisterEntry("08", "Next lyric page", "Main player and lyrics", "Scroll to later lyrics, if lyrics span pages", isApproved = true),
        ControlRegisterEntry("09", "DSP + EQ menu", "Theme, DSP and display settings", "Open audio effects and equalizer settings (icon-only closed)", isApproved = true),
        ControlRegisterEntry("10", "Settings", "Theme, DSP and display settings", "Open player settings", isApproved = true),
        ControlRegisterEntry("11", "Light theme", "Theme, DSP and display settings", "Apply light appearance", isApproved = true),
        ControlRegisterEntry("12", "Dark theme", "Theme, DSP and display settings", "Apply dark appearance", isApproved = true),
        ControlRegisterEntry("13", "Fit/layout", "Theme, DSP and display settings", "Choose how the background and player fit the screen", isApproved = true),
        ControlRegisterEntry("14", "Fullscreen", "Theme, DSP and display settings", "Toggle fullscreen playback", isApproved = true),
        ControlRegisterEntry("15", "Recording aspect ratio", "Theme, DSP and display settings", "Select 9:16 portrait or 16:9 landscape", isApproved = true),
        ControlRegisterEntry("16", "Seek bar", "Audio playback", "Seek to a selected audio position; update active lyric", isApproved = true),
        ControlRegisterEntry("17", "Like", "Audio playback", "Favorite current song", isApproved = true),
        ControlRegisterEntry("18", "Shuffle", "Audio playback", "Toggle randomized playlist order", isApproved = true),
        ControlRegisterEntry("19", "Previous track", "Audio playback", "Play previous song", isApproved = true),
        ControlRegisterEntry("20", "Play/pause", "Audio playback", "Start or pause current song", isApproved = true),
        ControlRegisterEntry("21", "Next track", "Audio playback", "Play next song", isApproved = true),
        ControlRegisterEntry("22", "Repeat", "Audio playback", "Toggle repeat-off, repeat-one, repeat-all", isApproved = true),
        ControlRegisterEntry("23", "Queue", "Audio playback", "Show upcoming playlist order", isApproved = true),
        ControlRegisterEntry("24", "Playlist tab", "Playlist and library", "Display loaded playlist", isApproved = true),
        ControlRegisterEntry("25", "Lyrics tab", "Playlist and library", "Display and edit current song lyrics and timing", isApproved = true),
        ControlRegisterEntry("26", "Library tab", "Playlist and library", "Browse songs loaded into the player", isApproved = true),
        ControlRegisterEntry("27", "Add Songs", "Playlist and library", "Load one or multiple local audio files", isApproved = true),
        ControlRegisterEntry("28", "Single Song Mode", "Playlist and library", "Hide playlist management during recording", isApproved = true),
        ControlRegisterEntry("29", "Auto Next", "Playlist and library", "Toggle automatic playback of the next song", isApproved = true),
        ControlRegisterEntry("30", "Previous playlist item", "Playlist and library", "Scroll playlist carousel backward", isApproved = true),
        ControlRegisterEntry("31", "Track card 1", "Playlist and library", "Select and play the displayed song (slot 1)", isApproved = true),
        ControlRegisterEntry("32", "Track card 2", "Playlist and library", "Select and play the displayed song (slot 2)", isApproved = true),
        ControlRegisterEntry("33", "Track card 3", "Playlist and library", "Select and play the displayed song (slot 3)", isApproved = true),
        ControlRegisterEntry("34", "Track card 4", "Playlist and library", "Select and play the displayed song (slot 4)", isApproved = true),
        ControlRegisterEntry("35", "Track card 5", "Playlist and library", "Select and play the displayed song (slot 5)", isApproved = true),
        ControlRegisterEntry("36", "Track card 6", "Playlist and library", "Select and play the displayed song (slot 6)", isApproved = true),
        ControlRegisterEntry("37", "Next playlist item", "Playlist and library", "Scroll playlist carousel forward", isApproved = true)
    )
}
