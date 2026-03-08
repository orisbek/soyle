package com.example.soyle.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("🦉", fontSize = 96.sp)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "Привет! Я Рыжик!",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text     = "Помогу тебе научиться красиво говорить!",
                fontSize = 16.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick  = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Начать!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}