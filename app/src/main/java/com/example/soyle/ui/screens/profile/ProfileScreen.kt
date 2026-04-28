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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.components.SoyleDivider
import com.example.soyle.ui.screens.settings.SettingsViewModel
import com.example.soyle.ui.theme.*

data class Badge(
    val id        : String,
    val icon      : String,
    val title     : String,
    val isUnlocked: Boolean
)

@Composable
fun ProfileScreen(
    onClose               : () -> Unit = {},
    onSignOut             : () -> Unit = {},
    onEditProfile         : () -> Unit = {},
    onOpenSettings        : () -> Unit = {},
    onOpenAboutMe         : () -> Unit = {},
    onOpenNotifications   : () -> Unit = {},
    profileViewModel      : ProfileViewModel  = hiltViewModel(),
    settingsViewModel     : SettingsViewModel = hiltViewModel()
) {
    val profileState  by profileViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()

    val progress    = profileState.progress
    val settings    = settingsState.settings
    val displayName = settings.displayName.ifBlank { profileState.userName }
    val avatarEmoji = settings.avatarEmoji

    val currentStreak = progress?.currentStreak ?: 0
    val longestStreak = progress?.longestStreak ?: 0
    val totalSessions = progress?.totalSessions ?: 0
    val totalXp       = progress?.totalXp ?: 0
    val level         = progress?.level ?: 1
    val xpInLevel     = totalXp % 500

    val animatedXp by animateFloatAsState(
        targetValue   = xpInLevel / 500f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label         = "xpAnim"
    )

    val badges = remember(currentStreak, longestStreak, totalXp, totalSessions) {
        listOf(
            Badge("first",   "🚀", "Первый шаг",   totalSessions >= 1),
            Badge("streak3", "🔥", "3 дня подряд", longestStreak >= 3),
            Badge("score85", "🏆", "Мастер звука",
                (progress?.phonemeScores?.get("Р") ?: 0f) >= 85f),
            Badge("streak7", "💎", "Неделя силы",  longestStreak >= 7),
            Badge("games10", "🎮", "10 занятий",   totalSessions >= 10),
            Badge("xp2000",  "⭐", "2000 XP",     totalXp >= 2000),
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(SoyleBg)) {

        // ── Шапка ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(SoyleSurface),
                contentAlignment = Alignment.Center
            ) { Text("🎁", fontSize = 18.sp) }

            Text("твой профиль.", fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = SoyleTextPrimary, letterSpacing = (-0.5).sp)

            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(SoyleSurface).clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) { Text("×", fontSize = 20.sp, color = SoyleTextSecondary) }
        }

        if (profileState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SoyleAccent, strokeWidth = 2.dp)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Карточка пользователя — тап → редактировать ───────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoyleSurface)
                    .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
                    .clickable(onClick = onEditProfile)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(SoyleSurface2)
                            .border(2.dp, SoyleAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(avatarEmoji, fontSize = 30.sp) }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(displayName.ifBlank { "Ученик" },
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SoyleTextPrimary)
                        Text("Уровень $level · $totalXp XP",
                            fontSize = 13.sp, color = SoyleTextSecondary)
                    }
                    Text("›", fontSize = 20.sp, color = SoyleTextMuted)
                }
            }

            // ── Премиум ───────────────────────────────────────────────────
            PremiumCard()

            SoyleDivider()

            // ── Серия и статистика ────────────────────────────────────────
            Text("СЕРИЯ И СТАТИСТИКА", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)

            StatRow("Расписание чек-ина", "Раз в день", showArrow = true)
            SoyleDivider()
            StatRow(
                label      = "Текущая серия",
                value      = (if (currentStreak > 0) "🔥 " else "") + streakLabel(currentStreak),
                valueColor = if (currentStreak > 0) SoyleStreak else SoyleTextSecondary,
                valueBold  = currentStreak > 0
            )
            SoyleDivider()
            StatRow("Лучшая серия", streakLabel(longestStreak))
            SoyleDivider()
            StatRow("Всего занятий", "$totalSessions")
            SoyleDivider()

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Уровень $level", fontSize = 15.sp, color = SoyleTextPrimary,
                        fontWeight = FontWeight.SemiBold)
                    Text("$xpInLevel / 500 XP", fontSize = 13.sp, color = SoyleTextSecondary)
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                        .clip(RoundedCornerShape(3.dp)).background(SoyleSurface2)
                ) {
                    Box(modifier = Modifier.fillMaxWidth(animatedXp).fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp)).background(SoyleAccent))
                }
            }

            SoyleDivider()

            // ── Достижения ────────────────────────────────────────────────
            Spacer(Modifier.height(4.dp))
            Text("ДОСТИЖЕНИЯ", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)
            Spacer(Modifier.height(4.dp))

            badges.chunked(3).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { badge -> BadgeItem(badge, Modifier.weight(1f)) }
                    if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            SoyleDivider()

            // ── Персонализация ────────────────────────────────────────────
            Text("ПЕРСОНАЛИЗАЦИЯ", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)

            ClickableRow(AppLanguage.aboutMe,       onClick = onOpenAboutMe)
            SoyleDivider()
            ClickableRow(AppLanguage.settings,      onClick = onOpenSettings)
            SoyleDivider()
            ClickableRow(AppLanguage.notifications, onClick = onOpenNotifications)
            SoyleDivider()

            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onSignOut).padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Выйти из аккаунта", fontSize = 15.sp, color = Color(0xFFFF6B6B))
                Text("›", fontSize = 16.sp, color = SoyleTextMuted)
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun StatRow(
    label      : String,
    value      : String,
    showArrow  : Boolean = false,
    valueColor : Color   = SoyleTextSecondary,
    valueBold  : Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically) {
        Text(label, fontSize = 15.sp, color = SoyleTextPrimary)
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, fontSize = 13.sp, color = valueColor,
                fontWeight = if (valueBold) FontWeight.SemiBold else FontWeight.Normal)
            if (showArrow) Text("›", fontSize = 16.sp, color = SoyleTextMuted)
        }
    }
}

@Composable
private fun ClickableRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, color = SoyleTextPrimary)
        Text("›", fontSize = 16.sp, color = SoyleTextMuted)
    }
}

@Composable
private fun PremiumCard() {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface).border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Открой все упражнения,\nAI-анализ речи и больше",
                    fontSize = 14.sp, color = SoyleTextPrimary, lineHeight = 20.sp)
                Text("С планом Söyle Premium", fontSize = 12.sp, color = SoyleTextMuted)
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(SoyleButtonPrimary).padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("7 дней бесплатно", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = SoyleButtonPrimaryText)
                }
            }
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(SoyleSurface2),
                contentAlignment = Alignment.Center) { Text("🔐", fontSize = 36.sp) }
        }
    }
}

@Composable
private fun BadgeItem(badge: Badge, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(SoyleSurface)
                .border(1.dp, if (badge.isUnlocked) SoyleTextMuted else SoyleBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(badge.icon, fontSize = 26.sp,
                color = if (badge.isUnlocked) Color.Unspecified else Color(0xFF2A2A2A))
        }
        Text(badge.title, fontSize = 10.sp, fontWeight = FontWeight.Medium,
            color = if (badge.isUnlocked) SoyleTextSecondary else SoyleTextDisabled,
            textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

private fun streakLabel(days: Int): String = when {
    days == 0            -> "0 дней"
    days % 100 in 11..19 -> "$days дней"
    days % 10 == 1       -> "$days день"
    days % 10 in 2..4   -> "$days дня"
    else                 -> "$days дней"
}
