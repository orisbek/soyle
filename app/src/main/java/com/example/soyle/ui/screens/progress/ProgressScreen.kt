package com.example.soyle.ui.screens.progress

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
import com.example.soyle.ui.theme.*

@Composable
fun ProgressScreen(onBack: () -> Unit) {

    val phonemes = remember {
        listOf(
            Triple("Р", 72f, 34),
            Triple("Л", 58f, 21),
            Triple("Ш", 85f, 15),
            Triple("З", 41f,  9),
        )
    }
    val weekData = remember {
        listOf("Пн" to 55f, "Вт" to 62f, "Ср" to 58f,
            "Чт" to 70f, "Пт" to 72f, "Сб" to 68f, "Вс" to 75f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidsBg)
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(KidsPurple, KidsBlue)))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.25f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text       = "📊 Мой прогресс",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Плитки статистики ──────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KidsStatTile("🏋️", "79",   "тренировок", KidsMint,   Modifier.weight(1f))
                KidsStatTile("🔥", "5 дн.", "серия",      KidsOrange, Modifier.weight(1f))
                KidsStatTile("⭐", "69%",   "ср. оценка", KidsPurple, Modifier.weight(1f))
            }

            // ── График недели ──────────────────────────────────────────────
            KidsSectionTitle("📅 Последние 7 дней")
            KidsWeekChart(data = weekData)

            // ── Прогресс по звукам ─────────────────────────────────────────
            KidsSectionTitle("🔤 Звуки")
            phonemes.forEach { (phoneme, score, attempts) ->
                KidsPhonemeCard(phoneme = phoneme, score = score, attempts = attempts)
            }

            // ── Достижения ─────────────────────────────────────────────────
            KidsSectionTitle("🏆 Достижения")
            KidsAchievements()

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun KidsSectionTitle(title: String) {
    Text(
        text       = title,
        fontSize   = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color      = KidsTextPrimary
    )
}

@Composable
private fun KidsStatTile(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .border(2.dp, color.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
        Text(label, fontSize = 10.sp, color = KidsTextSecondary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun KidsWeekChart(data: List<Pair<String, Float>>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(2.dp, KidsBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().height(90.dp),
            verticalAlignment     = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { (day, score) ->
                val animatedH by animateFloatAsState(
                    targetValue   = score / 100f * 70f,
                    animationSpec = tween(800, easing = FastOutSlowInEasing),
                    label         = "bar$day"
                )
                val barColor = scoreColor(score.toInt())
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(animatedH.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(barColor)
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(day, fontSize = 10.sp, color = KidsTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun KidsPhonemeCard(phoneme: String, score: Float, attempts: Int) {
    val color = scoreColor(score.toInt())
    val animatedScore by animateFloatAsState(
        targetValue   = score,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label         = "score$phoneme"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(2.dp, KidsBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Буква в кружке
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(phoneme, fontSize = 24.sp, fontWeight = FontWeight.Black, color = color)
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Звук «$phoneme»",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 15.sp,
                    color      = KidsTextPrimary
                )
                Text(
                    text       = "${animatedScore.toInt()}%",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 15.sp,
                    color      = color
                )
            }
            // Прогресс-бар
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedScore / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(Brush.horizontalGradient(listOf(color.copy(0.7f), color)))
                )
            }
            Text(
                text       = "$attempts занятий",
                fontSize   = 11.sp,
                color      = KidsTextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun KidsAchievements() {
    val items = listOf(
        Triple("🏆", "Первый звук",   true),
        Triple("🔥", "3 дня подряд",  true),
        Triple("⭐", "Оценка 90+",    false),
        Triple("🎯", "10 тренировок", true),
        Triple("💎", "7 дней подряд", false),
        Triple("🦜", "Мастер Р",      false),
    )

    items.chunked(3).forEach { row ->
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            row.forEach { (emoji, title, unlocked) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (unlocked) KidsYellowLight else Color(0xFFF5F5F5)
                        )
                        .border(
                            2.dp,
                            if (unlocked) KidsYellow else Color(0xFFE0E0E0),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text     = emoji,
                        fontSize = 28.sp,
                        color    = if (unlocked) Color.Unspecified else Color.Gray.copy(0.3f)
                    )
                    Text(
                        text       = title,
                        fontSize   = 10.sp,
                        fontWeight = if (unlocked) FontWeight.ExtraBold else FontWeight.Normal,
                        color      = if (unlocked) KidsTextPrimary else KidsTextDisabled,
                        textAlign  = TextAlign.Center
                    )
                    if (unlocked) Text("✓", fontSize = 12.sp, color = KidsGreen, fontWeight = FontWeight.Black)
                }
            }
            if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(10.dp))
    }
}
