package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.media.ElyzarethMediaLibraryService
import com.example.ui.ElyzarethPlayerApp
import com.example.viewmodel.LyricPlayerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LyricPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start media library service for background playback and Android Auto media session
        try {
            val serviceIntent = Intent(this, ElyzarethMediaLibraryService::class.java)
            startService(serviceIntent)
        } catch (_: Exception) {}

        setContent {
            ElyzarethPlayerApp(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
