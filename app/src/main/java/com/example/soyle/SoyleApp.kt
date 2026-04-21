package com.example.soyle

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.soyle.ui.navigation.SoyleNavGraph

@Composable
fun SoyleApp() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        SoyleNavGraph()
    }
}
