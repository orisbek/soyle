package com.example.soyle.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val level         = 4
    val totalXp       = 1820
    val xpInLevel     = totalXp % 500
    val currentStreak = 3
    val longestStreak = 7

    Scaffold(
        containerColor = DuoBg,
        topBar = {
            TopAppBar(
                title = { Text("Профиль", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DuoWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Аватар + уровень ───────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .border(2.dp, DuoBorder, RoundedCornerShape(20.dp))
                    .background(DuoWhite, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(DuoGreenLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦉", fontSize = 44.sp)
                }
                Text("Малыш", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DuoTextPrimary)
                DuoLevelBadge(level = level)

                // XP бар
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$xpInLevel XP", fontSize = 12.sp, color = DuoTextSecondary)
                        Text("500 XP", fontSize = 12.sp, color = DuoTextSecondary)
                    }
                    Spacer(Modifier.height(4.dp))
                    DuoProgressBar(progress = xpInLevel / 500f)
                }
            }

            // ── Streak ─────────────────────────────────────────────────────
            DuoSectionHeader("Серия дней")
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StreakTile("🔥", "$currentStreak", "Текущая серия", Modifier.weight(1f))
                StreakTile("🏆", "$longestStreak", "Рекорд",        Modifier.weight(1f))
                StreakTile("⭐", "${7 - currentStreak % 7}", "До награды", Modifier.weight(1f))
            }

            // ── Маскоты ────────────────────────────────────────────────────
            DuoSectionHeader("Маскоты")
            MascotRow(currentLevel = level)

            // ── Настройки ──────────────────────────────────────────────────
            DuoSectionHeader("Настройки")
            SettingsCard()

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StreakTile(emoji: String, value: String, label: String, modifier: Modifier) {
    Column(
        modifier            = modifier
            .border(2.dp, DuoBorder, RoundedCornerShape(16.dp))
            .background(DuoWhite, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DuoTextPrimary)
        Text(label, fontSize = 10.sp, color = DuoTextSecondary)
    }
}

@Composable
private fun MascotRow(currentLevel: Int) {
    val mascots = listOf("🦉" to 1, "🐻" to 3, "🐸" to 5, "🦊" to 8, "🐉" to 15)
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .border(2.dp, DuoBorder, RoundedCornerShape(16.dp))
            .background(DuoWhite, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        mascots.forEach { (emoji, req) ->
            val unlocked = currentLevel >= req
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            if (unlocked) DuoGreenLight else DuoGrayLight,
                            CircleShape
                        )
                        .then(if (emoji == "🦉") Modifier.border(2.dp, DuoGreen, CircleShape) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 26.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = if (unlocked) "✓" else "Ур.$req",
                    fontSize = 10.sp,
                    color    = if (unlocked) DuoGreen else DuoGray
                )
            }
        }
    }
}

@Composable
private fun SettingsCard() {
    var notif by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, DuoBorder, RoundedCornerShape(16.dp))
            .background(DuoWhite, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔔", fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
                Text("Напоминания", fontWeight = FontWeight.SemiBold, color = DuoTextPrimary)
            }
            Switch(
                checked         = notif,
                onCheckedChange = { notif = it },
                colors          = SwitchDefaults.colors(checkedThumbColor = DuoGreen, checkedTrackColor = DuoGreenLight)
            )
        }
        HorizontalDivider(color = DuoBorder)
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔊", fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
                Text("Звуки", fontWeight = FontWeight.SemiBold, color = DuoTextPrimary)
            }
            Switch(
                checked         = sound,
                onCheckedChange = { sound = it },
                colors          = SwitchDefaults.colors(checkedThumbColor = DuoGreen, checkedTrackColor = DuoGreenLight)
            )
        }
    }
}