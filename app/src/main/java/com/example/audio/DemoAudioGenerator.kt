package com.example.audio

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates an offline harmonic PCM WAV audio file for demo tracks.
 * Eliminates network dependencies and prevents UnknownHostException when playing demo tracks.
 */
object DemoAudioGenerator {
    private const val SAMPLE_RATE = 16000
    private const val CHANNELS = 1
    private const val BITS_PER_SAMPLE = 16

    @Synchronized
    fun getDemoAudioUri(context: Context, songId: String, durationMs: Long): Uri {
        val safeDurationMs = durationMs.coerceIn(5000L, 90000L)
        val file = File(context.cacheDir, "elyzareth_demo_${songId}.wav")
        if (file.exists() && file.length() > 44) {
            return Uri.fromFile(file)
        }

        try {
            val totalSamples = (SAMPLE_RATE * (safeDurationMs / 1000.0)).toInt()
            val dataSize = totalSamples * CHANNELS * (BITS_PER_SAMPLE / 8)

            FileOutputStream(file).use { fos ->
                // Write 44-byte standard RIFF PCM WAV Header
                val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
                header.put("RIFF".toByteArray())
                header.putInt(dataSize + 36)
                header.put("WAVE".toByteArray())
                header.put("fmt ".toByteArray())
                header.putInt(16) // subchunk1 size
                header.putShort(1) // PCM format
                header.putShort(CHANNELS.toShort())
                header.putInt(SAMPLE_RATE)
                header.putInt(SAMPLE_RATE * CHANNELS * (BITS_PER_SAMPLE / 8)) // byte rate
                header.putShort((CHANNELS * (BITS_PER_SAMPLE / 8)).toShort()) // block align
                header.putShort(BITS_PER_SAMPLE.toShort())
                header.put("data".toByteArray())
                header.putInt(dataSize)
                fos.write(header.array())

                // Write gentle musical chord melody (C maj7: 261Hz, 329Hz, 392Hz, 493Hz) with slow envelope
                val buffer = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN)
                val twoPi = 2.0 * PI
                var t = 0
                while (t < totalSamples) {
                    buffer.clear()
                    while (buffer.hasRemaining() && t < totalSamples) {
                        val timeSec = t.toDouble() / SAMPLE_RATE
                        // Chord progression: C - Am - F - G every 5 seconds
                        val chordRoot = when (((timeSec / 5.0).toInt()) % 4) {
                            0 -> 261.63 // C4
                            1 -> 220.00 // A3
                            2 -> 174.61 // F3
                            else -> 196.00 // G3
                        }
                        // Soft harmonic synthesis
                        val sampleVal = (
                            0.45 * sin(twoPi * chordRoot * timeSec) +
                            0.25 * sin(twoPi * (chordRoot * 1.25) * timeSec) +
                            0.20 * sin(twoPi * (chordRoot * 1.50) * timeSec) +
                            0.10 * sin(twoPi * (chordRoot * 2.00) * timeSec)
                        )
                        // Smooth envelope to avoid clicks
                        val envelope = sin((timeSec % 5.0) / 5.0 * PI).coerceIn(0.15, 1.0)
                        val finalSample = (sampleVal * envelope * 8000.0).toInt().coerceIn(-32767, 32767).toShort()
                        buffer.putShort(finalSample)
                        t++
                    }
                    fos.write(buffer.array(), 0, buffer.position())
                }
            }
        } catch (e: Exception) {
            Log.e("DemoAudioGenerator", "Failed to generate demo WAV: ${e.message}", e)
        }

        return Uri.fromFile(file)
    }
}
