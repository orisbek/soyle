package com.example.soyle.ui.screens.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Модели ────────────────────────────────────────────────────────────────────

data class Badge(
    val id          : String,
    val icon        : String,
    val title       : String,
    val description : String,
    val isUnlocked  : Boolean,
    val unlockedDate: String? = null
)

// ── Экран профиля ─────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(onClose: () -> Unit = {}) {
    val currentStreak = remember { 3 }
    val longestStreak = remember { 7 }
    val totalSessions = remember { 21 }
    val totalXp       = remember { 1850 }
    val level         = remember { 4 }
    val xpInLevel     = totalXp % 500

    val animatedXp by animateFloatAsState(
        targetValue   = xpInLevel / 500f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label         = "xpAnim"
    )

    val badges = remember {
        listOf(
            Badge("first",   "🚀", "Первый шаг",       "Завершил первый чек-ин",         true,  "20.04.2026"),
            Badge("streak3", "🔥", "3 дня подряд",     "Занимался 3 дня без перерыва",   true,  "22.04.2026"),
            Badge("score85", "🏆", "Мастер звука",     "Оценка 85%+ по звуку «Р»",       false),
            Badge("streak7", "💎", "Неделя силы",      "7 дней подряд",                  false),
            Badge("games10", "🎮", "Игровой маньяк",   "Сыграл 10 игр",                  false),
            Badge("xp2000",  "⭐", "Звёздный уровень", "Набрал 2000 XP",                 false),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // ── Шапка с кнопкой закрытия ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Бейдж подарка (как в Stoic)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoyleSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("🎁", fontSize = 18.sp)
            }

            Text(
                text          = "твой профиль.",
                fontWeight    = FontWeight.Bold,
                fontSize      = 20.sp,
                color         = SoyleTextPrimary,
                letterSpacing = (-0.5).sp
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoyleSurface)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Text("×", fontSize = 20.sp, color = SoyleTextSecondary)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Премиум-блок (как в Stoic) ────────────────────────────────
            PremiumCard()

            // ── Подарить ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoyleSurface)
                    .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Подари söyle", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = SoyleTextPrimary)
                        Text("тому, кто дорог.", fontSize = 13.sp, color = SoyleTextSecondary)
                    }
                    Text("✕", fontSize = 32.sp, color = SoyleTextMuted)
                }
            }

            SoyleDivider()

            // ── Серия дней ────────────────────────────────────────────────
            Text("СЕРИЯ И СТАТИСТИКА", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)

            // Чек-ин чекбоксы (как в Stoic — "One Daily")
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Расписание чек-ина", fontSize = 15.sp, color = SoyleTextPrimary)
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Раз в день", fontSize = 13.sp, color = SoyleTextSecondary)
                    Text("›", fontSize = 16.sp, color = SoyleTextMuted)
                }
            }

            SoyleDivider()

            // Стрик
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Текущая серия", fontSize = 15.sp, color = SoyleTextPrimary)
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔥", fontSize = 14.sp)
                    Text("$currentStreak дня", fontSize = 13.sp, color = SoyleStreak, fontWeight = FontWeight.SemiBold)
                }
            }

            SoyleDivider()

            // Рекорд
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Лучшая серия", fontSize = 15.sp, color = SoyleTextPrimary)
                Text("$longestStreak дней", fontSize = 13.sp, color = SoyleTextSecondary)
            }

            SoyleDivider()

            // Всего занятий
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Всего занятий", fontSize = 15.sp, color = SoyleTextPrimary)
                Text("$totalSessions", fontSize = 13.sp, color = SoyleTextSecondary)
            }

            SoyleDivider()

            // Уровень и XP
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Уровень $level", fontSize = 15.sp, color = SoyleTextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("$xpInLevel / 500 XP", fontSize = 13.sp, color = SoyleTextSecondary)
                }
                // XP-бар
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SoyleSurface2)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedXp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(SoyleAccent)
                    )
                }
            }

            SoyleDivider()

            // ── Достижения (бейджи) ───────────────────────────────────────
            Spacer(Modifier.height(4.dp))
            Text("ДОСТИЖЕНИЯ", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)
            Spacer(Modifier.height(4.dp))

            badges.chunked(3).forEach { row ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { badge ->
                        BadgeItem(badge = badge, modifier = Modifier.weight(1f))
                    }
                    if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            SoyleDivider()

            // ── Настройки ─────────────────────────────────────────────────
            Text("ПЕРСОНАЛИЗАЦИЯ", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)

            SettingsRow(label = "О себе")
            SoyleDivider()
            SettingsRow(label = "Настройки")
            SoyleDivider()
            SettingsRow(label = "Уведомления")

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ── Премиум-карточка ──────────────────────────────────────────────────────────

@Composable
private fun PremiumCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text      = "Открой все упражнения,\nAI-анализ речи и больше",
                    fontSize  = 14.sp,
                    color     = SoyleTextPrimary,
                    lineHeight = 20.sp
                )
                Text(
                    text     = "С планом Söyle Premium",
                    fontSize = 12.sp,
                    color    = SoyleTextMuted
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoyleButtonPrimary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text       = "7 дней бесплатно",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = SoyleButtonPrimaryText
                    )
                }
            }
            // Иллюстрация замка
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SoyleSurface2),
                contentAlignment = Alignment.Center
            ) {
                Text("🔐", fontSize = 36.sp)
            }
        }
    }
}

// ── Бейдж ─────────────────────────────────────────────────────────────────────

@Composable
private fun BadgeItem(badge: Badge, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кружок с иконкой
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SoyleSurface)
                .border(
                    1.dp,
                    if (badge.isUnlocked) SoyleTextMuted else SoyleBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badge.icon,
                fontSize = 26.sp,
                color = if (badge.isUnlocked) Color.Unspecified else Color(0xFF2A2A2A)
            )
        }
        Text(
            text      = badge.title,
            fontSize  = 10.sp,
            fontWeight = FontWeight.Medium,
            color     = if (badge.isUnlocked) SoyleTextSecondary else SoyleTextDisabled,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
        if (badge.isUnlocked && badge.unlockedDate != null) {
            Text(
                text      = badge.unlockedDate,
                fontSize  = 9.sp,
                color     = SoyleTextDisabled,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Строка настроек ───────────────────────────────────────────────────────────

@Composable
private fun SettingsRow(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 15.sp, color = SoyleTextPrimary)
        Text("›", fontSize = 16.sp, color = SoyleTextMuted)
    }
}