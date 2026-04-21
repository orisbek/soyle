package com.example.soyle.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.theme.*

// ── Главная кнопка — светлая пилюля (как в Stoic "Begin") ────────────────────

@Composable
fun SoylePrimaryButton(
    text     : String,
    modifier : Modifier = Modifier,
    enabled  : Boolean  = true,
    onClick  : () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(if (enabled) SoyleButtonPrimary else SoyleSurface2)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 16.sp,
            color      = if (enabled) SoyleButtonPrimaryText else SoyleTextMuted
        )
    }
}

// ── Тёмная кнопка-пилюля (для вариантов выбора) ──────────────────────────────

@Composable
fun SoyleOptionButton(
    text       : String,
    modifier   : Modifier = Modifier,
    isSelected : Boolean  = false,
    onClick    : () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isSelected) SoyleAccentSoft else SoyleSurface,
        label = "optionBg"
    )
    val borderColor by animateColorAsState(
        if (isSelected) SoyleAccent else SoyleBorder,
        label = "optionBorder"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize   = 15.sp,
            color      = if (isSelected) SoyleAccentLight else SoyleTextPrimary
        )
    }
}

// ── Карточка тёмная ───────────────────────────────────────────────────────────

@Composable
fun SoyleCard(
    modifier : Modifier = Modifier,
    onClick  : (() -> Unit)? = null,
    content  : @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(20.dp),
        content = content
    )
}

// ── Разделитель ───────────────────────────────────────────────────────────────

@Composable
fun SoyleDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier  = modifier,
        thickness = 1.dp,
        color     = SoyleBorder
    )
}

// ── Бейдж серии ──────────────────────────────────────────────────────────────

@Composable
fun StreakBadge(streak: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SoyleSurface2)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("🔥", fontSize = 14.sp)
        Text(
            text       = "$streak",
            fontWeight = FontWeight.Bold,
            fontSize   = 14.sp,
            color      = SoyleStreak
        )
    }
}

// ── Маленький тег ────────────────────────────────────────────────────────────

@Composable
fun SoyleTag(
    text    : String,
    modifier: Modifier = Modifier,
    color   : Color    = SoyleTextSecondary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SoyleSurface2)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

// ── Круговой прогресс ─────────────────────────────────────────────────────────

@Composable
fun CircularScore(
    score    : Int,
    modifier : Modifier = Modifier,
    size     : Dp       = 120.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue   = score / 100f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label         = "circProgress"
    )
    val color = speechScoreColor(score)

    Box(
        modifier         = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val r      = (size.toPx() - stroke) / 2
            val cx     = size.toPx() / 2
            val cy     = size.toPx() / 2
            // Track
            drawArc(
                color      = Color(0xFF222222),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                style      = Stroke(stroke, cap = StrokeCap.Round),
                topLeft    = androidx.compose.ui.geometry.Offset(cx - r, cy - r),
                size       = androidx.compose.ui.geometry.Size(r * 2, r * 2)
            )
            // Fill
            drawArc(
                color      = color,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter  = false,
                style      = Stroke(stroke, cap = StrokeCap.Round),
                topLeft    = androidx.compose.ui.geometry.Offset(cx - r, cy - r),
                size       = androidx.compose.ui.geometry.Size(r * 2, r * 2)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "$score%",
                fontWeight = FontWeight.Bold,
                fontSize   = (size.value * 0.2).sp,
                color      = color
            )
        }
    }
}

// ── SVG-like иллюстрация птицы (как в Stoic) — через Canvas ──────────────────

@Composable
fun BirdIllustration(modifier: Modifier = Modifier, alpha: Float = 1f) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val paint = Color.White.copy(alpha = alpha)
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)

        // Тело птицы (упрощённая геометрия)
        drawOval(
            color   = paint.copy(alpha = alpha * 0.9f),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.35f, h * 0.55f),
            size    = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.2f)
        )
        // Крыло
        drawArc(
            color      = paint,
            startAngle = 180f,
            sweepAngle = -120f,
            useCenter  = false,
            style      = stroke,
            topLeft    = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.5f),
            size       = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.15f)
        )
        // Голова
        drawCircle(
            color  = paint,
            radius = w * 0.06f,
            center = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.52f)
        )
    }
}

// ── Навигационная точка-прогресс (онбординг) ─────────────────────────────────

@Composable
fun OnboardingDot(active: Boolean, modifier: Modifier = Modifier) {
    val width by animateDpAsState(if (active) 24.dp else 8.dp, label = "dotW")
    Box(
        modifier = modifier
            .height(8.dp)
            .width(width)
            .clip(RoundedCornerShape(4.dp))
            .background(if (active) SoyleTextPrimary else SoyleTextMuted)
    )
}

// ── Тоггл-время (для напоминаний) ─────────────────────────────────────────────

@Composable
fun TimeToggleRow(
    label   : String,
    time    : String,
    enabled : Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(label, fontSize = 13.sp, color = SoyleTextSecondary)
            Text(
                text       = time,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = SoyleTextPrimary
            )
        }
        Switch(
            checked         = enabled,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = SoyleAccent,
                uncheckedThumbColor = SoyleTextMuted,
                uncheckedTrackColor = SoyleSurface2
            )
        )
    }
}

// ── Иконка-кружок активности ──────────────────────────────────────────────────

@Composable
fun ActivityCircle(
    icon     : String,
    label    : String,
    modifier : Modifier = Modifier,
    isLocked : Boolean  = false,
    onClick  : () -> Unit = {}
) {
    Column(
        modifier            = modifier.clickable(enabled = !isLocked, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SoyleSurface)
                .border(1.dp, SoyleBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = icon,
                fontSize = 26.sp,
                color    = if (isLocked) SoyleTextMuted else SoyleTextPrimary
            )
        }
        Text(
            text      = label,
            fontSize  = 11.sp,
            color     = if (isLocked) SoyleTextMuted else SoyleTextSecondary,
            textAlign = TextAlign.Center,
            maxLines  = 2
        )
    }
}