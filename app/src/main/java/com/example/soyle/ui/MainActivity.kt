package com.example.soyle.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.soyle.ui.navigation.NavGraph
import com.example.soyle.ui.theme.KidsBg
import com.example.soyle.ui.theme.SoyleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoyleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = KidsBg
                ) {
                    NavGraph()
                }
            }
        }
    }
}
