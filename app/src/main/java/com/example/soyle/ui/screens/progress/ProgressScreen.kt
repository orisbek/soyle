package com.example.soyle.ui.screens.progress

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(onBack: () -> Unit) {

    val phonemes = remember {
        listOf(
            Triple("Р", 72f, 34),
            Triple("Л", 58f, 21),
            Triple("Ш", 85f, 15),
            Triple("З", 41f, 9),
        )
    }
    val weekData = remember {
        listOf("Пн" to 55f, "Вт" to 62f, "Ср" to 58f,
            "Чт" to 70f, "Пт" to 72f, "Сб" to 68f, "Вс" to 75f)
    }

    Scaffold(
        containerColor = DuoBg,
        topBar = {
            TopAppBar(
                title = { Text("Мой прогресс", fontWeight = FontWeight.ExtraBold) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Статистика
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile("🏋️", "79",    "тренировок", DuoGreen,  Modifier.weight(1f))
                StatTile("🔥", "5 дн.", "серия",       DuoOrange, Modifier.weight(1f))
                StatTile("⭐", "69%",   "ср. оценка",  DuoBlue,   Modifier.weight(1f))
            }

            // График недели
            DuoSectionHeader("Последние 7 дней")
            WeekChart(data = weekData)

            // Прогресс по звукам
            DuoSectionHeader("Звуки")
            phonemes.forEach { (phoneme, score, attempts) ->
                ProgressCard(
                    phoneme  = phoneme,
                    score    = score,
                    attempts = attempts,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Достижения
            DuoSectionHeader("Достижения")
            AchievementGrid()

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatTile(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier            = modifier
            .border(2.dp, DuoBorder, RoundedCornerShape(16.dp))
            .background(DuoWhite, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = color)
        Text(label, fontSize = 10.sp, color = DuoTextSecondary)
    }
}

@Composable
private fun WeekChart(data: List<Pair<String, Float>>) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .border(2.dp, DuoBorder, RoundedCornerShape(16.dp))
            .background(DuoWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .height(100.dp),
        verticalAlignment     = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        data.forEach { (day, score) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height((score / 100f * 70).dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(scoreColor(score.toInt()))
                )
                Spacer(Modifier.height(4.dp))
                Text(day, fontSize = 9.sp, color = DuoTextSecondary)
            }
        }
    }
}

@Composable
private fun AchievementGrid() {
    val items = listOf(
        Triple("🏆", "Первый звук", true),
        Triple("🔥", "3 дня подряд", true),
        Triple("⭐", "Оценка 90+", false),
        Triple("🎯", "10 тренировок", true),
        Triple("💎", "7 дней подряд", false),
        Triple("🦉", "Мастер Р", false),
    )
    items.chunked(3).forEach { row ->
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            row.forEach { (emoji, title, unlocked) ->
                Column(
                    modifier            = Modifier
                        .weight(1f)
                        .border(2.dp, if (unlocked) DuoBorder else Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
                        .background(if (unlocked) DuoWhite else Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text  = emoji,
                        fontSize = 26.sp,
                        color = if (unlocked) Color.Unspecified else Color.Gray.copy(0.3f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = title,
                        fontSize   = 9.sp,
                        fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal,
                        color      = if (unlocked) DuoTextPrimary else DuoTextDisabled
                    )
                }
            }
            if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(10.dp))
    }
}