package com.example.soyle.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// Шаги онбординга
private data class OnboardingStep(
    val mascotText : String,
    val content    : @Composable () -> Unit
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var selectedPhoneme by remember { mutableStateOf<String?>(null) }
    var selectedLevel   by remember { mutableStateOf<String?>(null) }
    var selectedGoal    by remember { mutableStateOf<String?>(null) }

    val totalSteps = 3
    val canContinue = when (step) {
        0 -> true
        1 -> selectedPhoneme != null
        2 -> selectedLevel != null
        else -> false
    }

    fun next() {
        if (step < totalSteps - 1) step++ else onFinish()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoWhite)
            .padding(bottom = 24.dp)
    ) {
        // ── Top bar с прогрессом ───────────────────────────────────────────
        DuoTopBar(
            progress = (step + 1).toFloat() / totalSteps,
            onBack   = if (step > 0) ({ step-- }) else null
        )

        // ── Контент ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Маскот с пузырём
            AnimatedContent(targetState = step, label = "mascot") { s ->
                val text = when (s) {
                    0 -> "Привет! Я Рыжик! 🦉\nПомогу тебе научиться красиво говорить!"
                    1 -> "Какой звук будем учить?"
                    2 -> "Как ты сейчас произносишь этот звук?"
                    else -> ""
                }
                DuoMascotSpeech(text = text)
            }

            Spacer(Modifier.height(4.dp))

            // Контент шага
            AnimatedContent(
                targetState   = step,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                },
                label = "stepContent"
            ) { s ->
                when (s) {
                    0 -> Step0Welcome()
                    1 -> Step1PhonemeSelect(
                        selected = selectedPhoneme,
                        onSelect = { selectedPhoneme = it }
                    )
                    2 -> Step2LevelSelect(
                        selected = selectedLevel,
                        onSelect = { selectedLevel = it }
                    )
                }
            }
        }

        // ── Кнопка продолжить ──────────────────────────────────────────────
        DuoButton(
            text     = if (step == totalSteps - 1) "НАЧАТЬ!" else "ПРОДОЛЖИТЬ",
            enabled  = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            onClick  = { next() }
        )
    }
}

@Composable
private fun Step0Welcome() {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🦉", fontSize = 96.sp)
        Text(
            text       = "Логопедические тренировки",
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 22.sp,
            color      = DuoTextPrimary
        )
        Text(
            text     = "Учись говорить красиво каждый день",
            fontSize = 15.sp,
            color    = DuoTextSecondary
        )
    }
}

@Composable
private fun Step1PhonemeSelect(selected: String?, onSelect: (String) -> Unit) {
    val phonemes = listOf(
        "Р" to "🔴",
        "Л" to "🟡",
        "Ш" to "🟢",
        "З" to "🔵",
        "С" to "🟣",
        "Ж" to "🟠",
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        phonemes.forEach { (phoneme, emoji) ->
            DuoChoiceCard(
                text       = "Звук «$phoneme»",
                emoji      = emoji,
                isSelected = selected == phoneme,
                modifier   = Modifier.fillMaxWidth(),
                onClick    = { onSelect(phoneme) }
            )
        }
    }
}

@Composable
private fun Step2LevelSelect(selected: String?, onSelect: (String) -> Unit) {
    val levels = listOf(
        "beginner"     to ("Не умею произносить"    to "📊"),
        "intermediate" to ("Иногда получается"       to "📈"),
        "advanced"     to ("Почти правильно"         to "🎯"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        levels.forEach { (key, pair) ->
            val (label, emoji) = pair
            DuoChoiceCard(
                text       = label,
                emoji      = emoji,
                isSelected = selected == key,
                modifier   = Modifier.fillMaxWidth(),
                onClick    = { onSelect(key) }
            )
        }
    }
}