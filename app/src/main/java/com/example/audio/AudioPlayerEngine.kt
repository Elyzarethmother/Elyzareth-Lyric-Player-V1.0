package com.example.audio

import android.content.Context
import com.example.model.DspSettings
import com.example.model.Song
import kotlinx.coroutines.flow.StateFlow

/**
 * AudioPlayerEngine delegates directly to the shared ExoPlayer-powered ElyzarethPlaybackEngine.
 * This guarantees a single shared playback engine between the phone UI and Android Auto.
 */
class AudioPlayerEngine(context: Context) {
    val engine: ElyzarethPlaybackEngine = ElyzarethPlaybackEngine.getInstance(context)

    val isPlaying: StateFlow<Boolean> = engine.isPlaying
    val currentPositionMs: StateFlow<Long> = engine.currentPositionMs
    val durationMs: StateFlow<Long> = engine.durationMs
    val isEqSupported: StateFlow<Boolean> = engine.isEqSupported

    fun setOnCompletionListener(listener: () -> Unit) {
        engine.setOnCompletionListener(listener)
    }

    fun loadSong(song: Song) {
        engine.selectSong(song, playWhenReady = false)
    }

    fun play() {
        engine.play()
    }

    fun pause() {
        engine.pause()
    }

    fun togglePlayPause() {
        engine.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        engine.seekTo(positionMs)
    }

    fun stop() {
        engine.stop()
    }

    fun updateDsp(settings: DspSettings) {
        engine.updateDsp(settings)
    }

    fun release() {
        // We keep the shared engine alive for background playback and Android Auto
    }
}
