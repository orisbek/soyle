package com.example.soyle.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.soyle.ui.theme.*
import kotlinx.coroutines.delay

// ── Данные ачивок ──────────────────────────────────────────────────────────────

data class Achievement(
    val id       : String,
    val icon     : ImageVector,
    val color    : Color,
    val title    : String,
    val subtitle : String
)

val ALL_ACHIEVEMENTS = listOf(
    Achievement("first_step",   Icons.Outlined.RocketLaunch, Color(0xFF6C63FF), "Первый шаг",    "Так держать! Ты начал!"),
    Achievement("streak_3",     Icons.Outlined.Whatshot,     Color(0xFFFF6B35), "3 дня подряд",  "Огонь! Продолжай в том же духе!"),
    Achievement("master_sound", Icons.Outlined.EmojiEvents,  Color(0xFFFFD700), "Мастер звука",  "Твоё произношение становится лучше!"),
    Achievement("week_power",   Icons.Outlined.Diamond,      Color(0xFF00BCD4), "Неделя силы",   "7 дней занятий — ты герой!"),
    Achievement("sessions_10",  Icons.Outlined.SportsEsports,Color(0xFF4CAF50), "10 занятий",    "Настоящий чемпион тренировок!"),
    Achievement("xp_2000",      Icons.Outlined.Star,         Color(0xFFFF9800), "2000 XP",       "Огромный прогресс, гордимся тобой!"),
)

fun checkAchievement(
    streak      : Int,
    totalXp     : Int,
    sessionCount: Int,
    prevStreak  : Int,
    prevXp      : Int,
    prevSessions: Int
): Achievement? {
    return when {
        prevSessions == 0 && sessionCount >= 1                   -> ALL_ACHIEVEMENTS.first { it.id == "first_step" }
        prevStreak < 3   && streak >= 3                          -> ALL_ACHIEVEMENTS.first { it.id == "streak_3" }
        prevStreak < 7   && streak >= 7                          -> ALL_ACHIEVEMENTS.first { it.id == "week_power" }
        prevSessions < 10 && sessionCount >= 10                  -> ALL_ACHIEVEMENTS.first { it.id == "sessions_10" }
        prevXp < 2000    && totalXp >= 2000                      -> ALL_ACHIEVEMENTS.first { it.id == "xp_2000" }
        else                                                     -> null
    }
}

// ── Overlay компонент ─────────────────────────────────────────────────────────

@Composable
fun AchievementToastHost(
    achievement : Achievement?,
    onDismiss   : () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(achievement) {
        if (achievement != null) {
            visible = true
            delay(3500)
            visible = false
            delay(400)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible  = visible,
        enter    = slideInVertically { -it } + fadeIn(tween(300)),
        exit     = slideOutVertically { -it } + fadeOut(tween(300))
    ) {
        if (achievement != null) {
            AchievementBanner(achievement = achievement)
        }
    }
}

@Composable
private fun AchievementBanner(achievement: Achievement) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface)
            .border(1.dp, achievement.color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(achievement.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = achievement.icon,
                    contentDescription = null,
                    tint               = achievement.color,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "🏆 Достижение разблокировано!",
                    fontSize   = 10.sp,
                    color      = SoyleTextMuted,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text       = achievement.title,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = SoyleTextPrimary,
                    modifier   = Modifier.padding(top = 2.dp)
                )
                Text(
                    text     = achievement.subtitle,
                    fontSize = 12.sp,
                    color    = SoyleTextSecondary
                )
            }

            Icon(
                imageVector        = Icons.Outlined.EmojiEvents,
                contentDescription = null,
                tint               = achievement.color,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}
