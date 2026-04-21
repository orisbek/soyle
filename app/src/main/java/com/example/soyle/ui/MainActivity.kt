package com.example.soyle.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.soyle.ui.navigation.SoyleNavGraph
import com.example.soyle.ui.theme.SoyleTheme
import dagger.hilt.android.AndroidEntryPoint

// ── MainActivity ──────────────────────────────────────────────────────────────

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge — контент рисуется под системными барами
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SoyleTheme {
                SoyleNavGraph(
                    startDestination = "onboarding" // или "home" после первого запуска
                )
            }
        }
    }
}