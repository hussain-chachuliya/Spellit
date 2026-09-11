package com.spellit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.spellit.presentation.navigation.SpellItNavHost
import com.spellit.presentation.theme.SpellItTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* results handled lazily; playback falls back gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestUpfrontPermissions()
        setContent {
            SpellItTheme {
                LaunchedEffect(Unit) {
                    requestMicrophoneIfMissing()
                }
                SpellItNavHost()
            }
        }
    }

    private fun requestUpfrontPermissions() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        permissionLauncher.launch(permissions)
    }

    private fun requestMicrophoneIfMissing() {
        val context = applicationContext
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
        }
    }
}