package com.example.soyle.ui.screens.result

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.components.ScoreBar

@Composable
fun ResultScreen(
    score   : Int,
    phoneme : String,
    onRetry : () -> Unit,
    onHome  : () -> Unit
) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text       = "Результат",
            fontSize   = 24.sp,
            fontWeight = FontWeight.Bold
        )

        ScoreBar(score = score)

        Text(
            text     = "Звук «$phoneme»",
            fontSize = 20.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick  = onRetry,
                modifier = Modifier.weight(1f).height(52.dp)
            ) {
                Text("Ещё раз", fontSize = 16.sp)
            }
            Button(
                onClick  = onHome,
                modifier = Modifier.weight(1f).height(52.dp)
            ) {
                Text("Дальше →", fontSize = 16.sp)
            }
        }
    }
}