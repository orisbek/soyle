package com.example.soyle.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// 1. DuoButton — главная кнопка с 3D-эффектом (тень снизу)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Основная кнопка в стиле Duolingo.
 * - Активная: ярко-зелёная с тёмно-зелёной «тенью» снизу
 * - Неактивная: серая
 *
 * Использование:
 * ```
 * DuoButton(text = "ПРОДОЛЖИТЬ", enabled = canContinue) { onContinue() }
 * ```
 */
@Composable
fun DuoButton(
    text     : String,
    modifier : Modifier = Modifier,
    enabled  : Boolean  = true,
    color    : Color    = DuoGreen,
    shadowColor: Color  = DuoGreenDark,
    onClick  : () -> Unit
) {
    val bgColor     = if (enabled) color      else DuoGrayLight
    val shadowClr   = if (enabled) shadowColor else Color(0xFFBDBDBD)
    val textColor   = if (enabled) DuoWhite    else DuoGray

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() }
    ) {
        // Тень (3D-эффект снизу)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp)
                .background(shadowClr, RoundedCornerShape(16.dp))
        )
        // Основная кнопка
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = text,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 16.sp,
                color      = textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. DuoOutlineButton — вторичная кнопка (белая с обводкой)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoOutlineButton(
    text    : String,
    modifier: Modifier = Modifier,
    enabled : Boolean  = true,
    onClick : () -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, if (enabled) DuoBorder else DuoGrayLight, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 16.sp,
            color      = if (enabled) DuoTextPrimary else DuoGray,
            letterSpacing = 0.5.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. DuoChoiceCard — карточка выбора (как в Duolingo)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Карточка с вариантом ответа.
 * - Обычная: белый фон, серая обводка
 * - Выбранная: голубой фон, синяя обводка, синий текст
 *
 * Использование:
 * ```
 * DuoChoiceCard(
 *     text = "Звук «Р»",
 *     emoji = "🔤",
 *     isSelected = selected == "Р",
 *     onClick = { selected = "Р" }
 * )
 * ```
 */
@Composable
fun DuoChoiceCard(
    text       : String,
    modifier   : Modifier = Modifier,
    emoji      : String?  = null,
    isSelected : Boolean  = false,
    onClick    : () -> Unit
) {
    val bgColor     by animateColorAsState(
        if (isSelected) DuoBlueLight else DuoWhite,
        label = "cardBg"
    )
    val borderColor by animateColorAsState(
        if (isSelected) DuoBorderSelected else DuoBorder,
        label = "cardBorder"
    )
    val textColor   by animateColorAsState(
        if (isSelected) DuoBlueDark else DuoTextPrimary,
        label = "cardText"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (emoji != null) {
                Text(emoji, fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text       = text,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = textColor,
                modifier   = Modifier.weight(1f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. DuoProgressBar — прогресс-бар сверху экрана (как в Duolingo)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Зелёный прогресс-бар вверху экрана.
 *
 * Использование:
 * ```
 * DuoProgressBar(progress = 0.4f) // 40%
 * ```
 */
@Composable
fun DuoProgressBar(
    progress : Float,           // 0.0 – 1.0
    modifier : Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue   = progress.coerceIn(0f, 1f),
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DuoProgressBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .background(DuoProgressFill, RoundedCornerShape(8.dp))
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. DuoMascotSpeech — маскот с речевым пузырём
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Маскот (совёнок) с пузырём речи слева или справа.
 *
 * Использование:
 * ```
 * DuoMascotSpeech(text = "Скажи звук «Р»!")
 * ```
 */
@Composable
fun DuoMascotSpeech(
    text     : String,
    modifier : Modifier = Modifier,
    mascotEmoji: String = "🦉"
) {
    Row(
        modifier          = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Маскот
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFF7F7F7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(mascotEmoji, fontSize = 42.sp)
        }

        Spacer(Modifier.width(12.dp))

        // Речевой пузырь
        Box(
            modifier = Modifier
                .background(DuoWhite, RoundedCornerShape(
                    topStart    = 4.dp,
                    topEnd      = 16.dp,
                    bottomEnd   = 16.dp,
                    bottomStart = 16.dp
                ))
                .border(2.dp, DuoBorder, RoundedCornerShape(
                    topStart    = 4.dp,
                    topEnd      = 16.dp,
                    bottomEnd   = 16.dp,
                    bottomStart = 16.dp
                ))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text       = text,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = DuoTextPrimary,
                lineHeight = 22.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. DuoXpBadge — бейдж с XP очками
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoXpBadge(xp: Int, modifier: Modifier = Modifier) {
    Row(
        modifier          = modifier
            .background(DuoYellowLight, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⚡", fontSize = 14.sp)
        Spacer(Modifier.width(4.dp))
        Text(
            text       = "+$xp XP",
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 14.sp,
            color      = DuoYellow
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. DuoStreakBadge — бейдж серии дней
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoStreakBadge(streak: Int, modifier: Modifier = Modifier) {
    Row(
        modifier          = modifier
            .background(DuoYellowLight, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔥", fontSize = 14.sp)
        Spacer(Modifier.width(4.dp))
        Text(
            text       = "$streak",
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 14.sp,
            color      = DuoOrange
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. DuoHeartRow — жизни в виде сердечек
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoHeartRow(lives: Int, maxLives: Int = 3, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(maxLives) { i ->
            Text(
                text     = if (i < lives) "❤️" else "🖤",
                fontSize = 20.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 9. DuoScoreCircle — круговой индикатор результата
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoScoreCircle(
    score    : Int,
    modifier : Modifier = Modifier,
    size     : Dp       = 140.dp
) {
    val animatedScore by animateFloatAsState(
        targetValue   = score.toFloat(),
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "score"
    )
    val color = scoreColor(score)

    Box(
        modifier         = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress    = { animatedScore / 100f },
            modifier    = Modifier.fillMaxSize(),
            color       = color,
            trackColor  = color.copy(alpha = 0.15f),
            strokeWidth = 10.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "${animatedScore.toInt()}%",
                fontWeight = FontWeight.Black,
                fontSize   = 32.sp,
                color      = color
            )
            Text(
                text     = scoreLabel(score),
                fontSize = 12.sp,
                color    = DuoTextSecondary
            )
        }
    }
}

private fun scoreLabel(score: Int) = when {
    score >= 85 -> "Отлично! 🎉"
    score >= 65 -> "Хорошо! 😊"
    score >= 45 -> "Старайся 🙂"
    else        -> "Ещё раз 💪"
}

// ─────────────────────────────────────────────────────────────────────────────
// 10. DuoLevelBadge — бейдж уровня
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoLevelBadge(level: Int, modifier: Modifier = Modifier) {
    val color = levelColor(level)
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .border(2.dp, color, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = "Ур. $level",
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 13.sp,
            color      = color
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 11. DuoSectionHeader — заголовок секции
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text       = title,
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 16.sp,
        color      = DuoTextPrimary,
        modifier   = modifier
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 12. DuoTopBar — верхняя панель с прогресс-баром и кнопкой назад
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DuoTopBar(
    progress    : Float? = null,   // null = не показывать прогресс-бар
    onBack      : (() -> Unit)? = null,
    trailing    : @Composable (() -> Unit)? = null,
    modifier    : Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Text("←", fontSize = 20.sp, color = DuoGray)
                }
            } else {
                Spacer(Modifier.size(40.dp))
            }

            if (progress != null) {
                DuoProgressBar(
                    progress = progress,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            if (trailing != null) {
                trailing()
            } else {
                Spacer(Modifier.size(40.dp))
            }
        }
    }
}