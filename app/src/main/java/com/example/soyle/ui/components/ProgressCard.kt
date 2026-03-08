package com.example.soyle.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.theme.*

@Composable
fun ProgressCard(
    phoneme   : String,
    score     : Float,
    attempts  : Int,
    trend     : Float    = 0f,
    modifier  : Modifier = Modifier
) {
    val animScore by animateFloatAsState(
        targetValue   = score,
        animationSpec = tween(800, easing = EaseOutCubic),
        label         = "score"
    )
    val barColor = scoreColor(score.toInt())

    Row(
        modifier          = modifier
            .border(2.dp, DuoBorder, RoundedCornerShape(16.dp))
            .background(DuoWhite, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Буква в кружке
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(DuoGreenLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(phoneme, fontWeight = FontWeight.Black, fontSize = 22.sp, color = DuoGreen)
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Звук «$phoneme»", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DuoTextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (trend != 0f) {
                        Text(
                            text       = if (trend > 0) "+${trend.toInt()}%" else "${trend.toInt()}%",
                            fontSize   = 11.sp,
                            color      = if (trend > 0) DuoGreen else DuoRed,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text("${animScore.toInt()}%", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = barColor)
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(DuoProgressBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animScore / 100f)
                        .fillMaxHeight()
                        .background(barColor, RoundedCornerShape(4.dp))
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("$attempts попыток", fontSize = 11.sp, color = DuoTextSecondary)
        }
    }
}