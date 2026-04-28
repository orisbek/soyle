package com.example.soyle.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.example.soyle.ui.navigation.SoyleNavGraph
import com.example.soyle.ui.screens.settings.SettingsViewModel
import com.example.soyle.ui.theme.SoyleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Грузит тему и язык из Firestore при каждом запуске
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge — контент рисуется под системными барами
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SoyleTheme {
                SoyleNavGraph()
            }
        }
    }
}