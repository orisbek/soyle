package com.example.soyle.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var step            by remember { mutableIntStateOf(0) }
    var selectedPhoneme by remember { mutableStateOf<String?>(null) }
    var selectedLevel   by remember { mutableStateOf<String?>(null) }

    val totalSteps  = 3
    val canContinue = when (step) {
        0    -> true
        1    -> selectedPhoneme != null
        2    -> selectedLevel != null
        else -> false
    }

    val gradients = listOf(
        listOf(KidsMint, KidsBlue),
        listOf(KidsPurple, KidsPink),
        listOf(KidsOrange, KidsYellow)
    )
    val gradient = gradients[step.coerceIn(0, 2)]

    // Попугай прыгает
    val infiniteTransition = rememberInfiniteTransition(label = "onboard")
    val mascotY by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotY"
    )

    Column(
        modifier = Modifier.fillMaxSize().background(KidsBg)
    ) {
        // ── Градиентная шапка ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable { step-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(12.dp))
                }
                // Точки прогресса
                Row(
                    modifier              = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    repeat(totalSteps) { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 28.dp else 10.dp)
                                .clip(if (i == step) RoundedCornerShape(6.dp) else CircleShape)
                                .background(
                                    if (i <= step) Color.White
                                    else Color.White.copy(alpha = 0.35f)
                                )
                                .animateContentSize()
                        )
                        if (i < totalSteps - 1) Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }

        Column(
            modifier            = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Маскот ────────────────────────────────────────────────────
            AnimatedContent(targetState = step, label = "mascot") { s ->
                val text = when (s) {
                    0    -> "Привет! Я Рыжик!\nПомогу тебе красиво говорить! 🎉"
                    1    -> "Какой звук будем учить?"
                    2    -> "Как ты сейчас произносишь этот звук?"
                    else -> ""
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(listOf(KidsMintLight, KidsBlueLight)))
                        .border(3.dp, KidsMint.copy(0.4f), RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("🦜", fontSize = 52.sp, modifier = Modifier.offset(y = mascotY.dp))
                    Text(
                        text       = text,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = KidsTextPrimary,
                        lineHeight = 24.sp,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }

            // ── Контент шага ──────────────────────────────────────────────
            AnimatedContent(
                targetState   = step,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                },
                label = "stepContent"
            ) { s ->
                when (s) {
                    0    -> OnboardingWelcome()
                    1    -> OnboardingPhonemes(selected = selectedPhoneme) { selectedPhoneme = it }
                    2    -> OnboardingLevels(selected = selectedLevel) { selectedLevel = it }
                    else -> {}
                }
            }
        }

        // ── Кнопка продолжить ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            val btnGradient = if (canContinue)
                Brush.horizontalGradient(gradient)
            else
                Brush.horizontalGradient(listOf(Color(0xFFCCCCCC), Color(0xFFBBBBBB)))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(btnGradient)
                    .clickable(enabled = canContinue) {
                        if (step < totalSteps - 1) step++ else onFinish()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = if (step == totalSteps - 1) "🚀  НАЧАТЬ!" else "ПРОДОЛЖИТЬ  →",
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            }
        }
    }
}

@Composable
private fun OnboardingWelcome() {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("🦜", fontSize = 96.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

        Text(
            text       = "Söyle",
            fontWeight = FontWeight.Black,
            fontSize   = 42.sp,
            color      = KidsMint,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.fillMaxWidth()
        )
        Text(
            text       = "Логопедические тренировки\nдля детей!",
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = KidsTextPrimary,
            textAlign  = TextAlign.Center,
            lineHeight = 28.sp,
            modifier   = Modifier.fillMaxWidth()
        )

        // Фишки
        val features = listOf(
            "🎮" to "Весёлые игры",
            "🎙" to "Практика звуков",
            "🏆" to "Достижения и награды"
        )
        Column(
            modifier            = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            features.forEach { (emoji, text) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(KidsMintLight)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(emoji, fontSize = 24.sp)
                    Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = KidsTextPrimary)
                }
            }
        }
    }
}

@Composable
private fun OnboardingPhonemes(selected: String?, onSelect: (String) -> Unit) {
    val phonemes = listOf(
        Triple("Р", "🔴", KidsPink),
        Triple("Л", "🟡", KidsYellow),
        Triple("Ш", "🟢", KidsGreen),
        Triple("З", "🔵", KidsBlue),
        Triple("С", "🟣", KidsPurple),
        Triple("Ж", "🟠", KidsOrange),
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        phonemes.forEach { (phoneme, emoji, color) ->
            val isSelected = selected == phoneme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) color.copy(alpha = 0.15f) else Color.White)
                    .border(
                        width = if (isSelected) 3.dp else 2.dp,
                        color = if (isSelected) color else KidsBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onSelect(phoneme) }
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = if (isSelected) 0.2f else 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(phoneme, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
                }
                Text(
                    text       = "Звук «$phoneme»",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (isSelected) color else KidsTextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                if (isSelected) Text("✓", fontSize = 18.sp, color = color, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun OnboardingLevels(selected: String?, onSelect: (String) -> Unit) {
    val levels = listOf(
        Triple("beginner",     "Ещё не получается 😅",   KidsPink),
        Triple("intermediate", "Иногда выходит 🙂",       KidsYellow),
        Triple("advanced",     "Почти правильно! 😊",     KidsGreen),
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        levels.forEach { (key, label, color) ->
            val isSelected = selected == key
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) color.copy(0.12f) else Color.White)
                    .border(
                        width = if (isSelected) 3.dp else 2.dp,
                        color = if (isSelected) color else KidsBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onSelect(key) }
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (key) {
                            "beginner"     -> "🌱"
                            "intermediate" -> "🌿"
                            else           -> "🌳"
                        },
                        fontSize = 24.sp
                    )
                }
                Text(
                    text       = label,
                    fontSize   = 15.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color      = if (isSelected) color else KidsTextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                if (isSelected) Text("✓", fontSize = 18.sp, color = color, fontWeight = FontWeight.Black)
            }
        }
    }
}
