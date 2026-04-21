package com.example.soyle.ui.screens.checkin

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Шаги чек-ина ─────────────────────────────────────────────────────────────

@Composable
fun CheckInScreen(
    onFinish : () -> Unit = {},
    onSkip   : () -> Unit = {}
) {
    var step       by remember { mutableIntStateOf(0) }
    var mood       by remember { mutableIntStateOf(-1) }
    var restLevel  by remember { mutableFloatStateOf(0.5f) }
    var focusAreas by remember { mutableStateOf(setOf<String>()) }

    val steps = 3

    AnimatedContent(
        targetState   = step,
        transitionSpec = {
            slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
        },
        label = "checkInStep"
    ) { currentStep ->
        when (currentStep) {
            0 -> MoodStep(
                selected = mood,
                onSelect = { mood = it },
                onNext   = { step++ },
                onSkip   = onSkip
            )
            1 -> RestStep(
                value    = restLevel,
                onChange = { restLevel = it },
                onNext   = { step++ },
                onSkip   = onSkip
            )
            2 -> FocusStep(
                selected = focusAreas,
                onToggle = { area ->
                    focusAreas = if (area in focusAreas) focusAreas - area else focusAreas + area
                },
                onNext   = onFinish,
                onSkip   = onSkip
            )
        }
    }
}

// ── Шаг 1: Настроение (emoji-ряд) ────────────────────────────────────────────

@Composable
private fun MoodStep(
    selected : Int,
    onSelect : (Int) -> Unit,
    onNext   : () -> Unit,
    onSkip   : () -> Unit
) {
    val moods = listOf("😞", "😕", "😐", "🙂", "😄")

    CheckInLayout(onSkip = onSkip, canNext = selected >= 0, onNext = onNext) {
        Spacer(Modifier.weight(1f))

        Text(
            text      = "Как чувствуешь\nсебя сегодня?",
            fontWeight = FontWeight.Bold,
            fontSize  = 28.sp,
            color     = SoyleTextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            letterSpacing = (-0.5).sp,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(60.dp))

        // Ряд emoji-кружков
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            moods.forEachIndexed { index, emoji ->
                val isSelected = selected == index
                val scale by animateFloatAsState(
                    if (isSelected) 1.3f else 1f,
                    label = "moodScale$index"
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) SoyleButtonPrimary else SoyleSurface)
                        .border(1.dp, if (isSelected) Color.Transparent else SoyleBorder, CircleShape)
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 24.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Ужасно", fontSize = 11.sp, color = SoyleTextMuted)
            Text("Отлично", fontSize = 11.sp, color = SoyleTextMuted)
        }

        Spacer(Modifier.weight(2f))
    }
}

// ── Шаг 2: Отдых/усталость (слайдер) ─────────────────────────────────────────

@Composable
private fun RestStep(
    value    : Float,
    onChange : (Float) -> Unit,
    onNext   : () -> Unit,
    onSkip   : () -> Unit
) {
    val levelText = when {
        value < 0.2f -> "совсем без сил"
        value < 0.4f -> "немного устал"
        value < 0.6f -> "умеренно отдохнул"
        value < 0.8f -> "хорошо отдохнул"
        else         -> "полон энергии"
    }

    CheckInLayout(onSkip = onSkip, canNext = true, onNext = onNext) {
        Spacer(Modifier.weight(1f))

        Text(
            text      = "Как хорошо ты\nотдохнул сегодня?",
            fontWeight = FontWeight.Bold,
            fontSize  = 28.sp,
            color     = SoyleTextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            letterSpacing = (-0.5).sp,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(60.dp))

        // Динамический текст
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text     = "Сегодня я чувствую себя",
                fontSize = 16.sp,
                color    = SoyleTextSecondary
            )
            Text(
                text       = levelText + ".",
                fontWeight = FontWeight.Bold,
                fontSize   = 22.sp,
                color      = SoyleTextPrimary
            )
        }

        Spacer(Modifier.height(32.dp))

        // Слайдер (как в Stoic RestStep)
        Slider(
            value         = value,
            onValueChange = onChange,
            modifier      = Modifier.fillMaxWidth(),
            colors        = SliderDefaults.colors(
                thumbColor            = SoyleButtonPrimary,
                activeTrackColor      = SoyleButtonPrimary,
                inactiveTrackColor    = SoyleSurface2
            )
        )

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Совсем нет", fontSize = 11.sp, color = SoyleTextMuted)
            Text("Очень хорошо", fontSize = 11.sp, color = SoyleTextMuted)
        }

        Spacer(Modifier.weight(2f))
    }
}

// ── Шаг 3: Фокус дня (сетка категорий) ───────────────────────────────────────

@Composable
private fun FocusStep(
    selected : Set<String>,
    onToggle : (String) -> Unit,
    onNext   : () -> Unit,
    onSkip   : () -> Unit
) {
    val areas = listOf(
        "💼" to "Учёба",
        "☀️" to "Звуки",
        "👥" to "Общение",
        "🎨" to "Творчество",
        "🏠" to "Дома",
        "📚" to "Чтение",
        "🎉" to "Игры",
        "🌙" to "Отдых",
        "🌿" to "Природа"
    )

    CheckInLayout(onSkip = onSkip, canNext = true, onNext = onNext) {
        Spacer(Modifier.height(20.dp))

        Text(
            text      = "На чём хочешь\nсосредоточиться?",
            fontWeight = FontWeight.Bold,
            fontSize  = 28.sp,
            color     = SoyleTextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            letterSpacing = (-0.5).sp,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // Сетка 3×3
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            areas.chunked(3).forEach { row ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { (icon, label) ->
                        val isSelected = label in selected
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) SoyleSurface3 else SoyleSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) SoyleBorderLight else SoyleBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { onToggle(label) }
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(icon, fontSize = 24.sp)
                            Text(
                                text     = label,
                                fontSize = 11.sp,
                                color    = if (isSelected) SoyleTextPrimary else SoyleTextSecondary,
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Кнопка персонализации
        Row(
            modifier              = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SoyleSurface)
                .border(1.dp, SoyleBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("✏️", fontSize = 14.sp)
            Text("Настроить", fontSize = 13.sp, color = SoyleTextSecondary)
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ── Базовый layout чек-ина ────────────────────────────────────────────────────

@Composable
private fun CheckInLayout(
    onSkip  : () -> Unit,
    canNext : Boolean,
    onNext  : () -> Unit,
    content : @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // Кнопка ×
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(SoyleSurface)
                .clickable(onClick = onSkip),
            contentAlignment = Alignment.Center
        ) {
            Text("×", fontSize = 20.sp, color = SoyleTextSecondary)
        }

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }

        // Нижняя навигация
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            if (!canNext) {
                Text(
                    text    = "Пропустить",
                    fontSize = 14.sp,
                    color   = SoyleTextSecondary,
                    modifier = Modifier.clickable(onClick = onNext)
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (canNext) SoyleButtonPrimary else SoyleSurface2)
                    .clickable(enabled = canNext, onClick = onNext),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = "›",
                    fontSize = 24.sp,
                    color    = if (canNext) SoyleButtonPrimaryText else SoyleTextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}