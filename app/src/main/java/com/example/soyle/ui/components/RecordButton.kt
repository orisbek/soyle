package com.example.soyle.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordButton(
    isRecording : Boolean,
    enabled     : Boolean = true,
    onClick     : () -> Unit
) {
    // Пульсация во время записи
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 1.15f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val scale = if (isRecording) pulseScale else 1f
    val color = if (isRecording) Color(0xFFE53935) else Color(0xFFFFC107)

    Button(
        onClick        = onClick,
        enabled        = enabled,
        modifier       = Modifier
            .size(96.dp)
            .scale(scale),
        shape          = CircleShape,
        colors         = ButtonDefaults.buttonColors(containerColor = color),
        elevation      = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Icon(
            imageVector        = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = if (isRecording) "Стоп" else "Записать",
            modifier           = Modifier.size(40.dp),
            tint               = Color.White
        )
    }
}