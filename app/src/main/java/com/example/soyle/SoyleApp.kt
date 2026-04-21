package com.example.soyle

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.soyle.ui.navigation.NavGraph

@Composable
fun SoyleApp() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Добавляем padding от Scaffold, если нужно, или передаем в NavGraph
        // В данном случае просто вызываем NavGraph
        NavGraph()
    }
}
