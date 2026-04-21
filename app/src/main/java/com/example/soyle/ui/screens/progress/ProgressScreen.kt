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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.domain.model.Achievement
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = KidsBg,
        bottomBar = {
            KidsFloatingBottomBar(
                currentRoute = "progress",
                onHome = onOpenHome,
                onProgress = {},
                onProfile = onOpenProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
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

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = KidsPurple)
                }
            } else {
                val progress = uiState.progress
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
                        KidsStatTile("🏋️", "${progress?.totalSessions ?: 0}", "тренировок", KidsMint, Modifier.weight(1f))
                        KidsStatTile("🔥", "${progress?.currentStreak ?: 0} дн.", "серия", KidsOrange, Modifier.weight(1f))
                        val avgScore = progress?.phonemeScores?.values?.average()?.toInt() ?: 0
                        KidsStatTile("⭐", "$avgScore%", "ср. оценка", KidsPurple, Modifier.weight(1f))
                    }

                    // ── Прогресс по звукам ─────────────────────────────────────────
                    KidsSectionTitle("🔤 Мои звуки")
                    if (progress?.phonemeScores.isNullOrEmpty()) {
                        Text(
                            "Здесь появится прогресс после первых занятий!",
                            color = KidsTextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                        )
                    } else {
                        progress?.phonemeScores?.forEach { (phoneme, score) ->
                            KidsPhonemeCard(phoneme = phoneme, score = score, attempts = 0)
                        }
                    }

                    // ── Достижения ─────────────────────────────────────────────────
                    KidsSectionTitle("🏆 Достижения")
                    KidsAchievements(progress?.achievements ?: emptyList())

                    Spacer(Modifier.height(20.dp))
                }
            }
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
        }
    }
}

@Composable
private fun KidsAchievements(achievements: List<Achievement>) {
    if (achievements.isEmpty()) {
        Text("Тренируйся, чтобы открывать достижения!", color = KidsTextSecondary, fontSize = 12.sp)
        return
    }

    achievements.chunked(3).forEach { row ->
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            row.forEach { achievement ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (achievement.isUnlocked) KidsYellowLight else Color(0xFFF5F5F5)
                        )
                        .border(
                            2.dp,
                            if (achievement.isUnlocked) KidsYellow else Color(0xFFE0E0E0),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text     = achievement.id.take(2), // placeholder for icon
                        fontSize = 28.sp,
                        color    = if (achievement.isUnlocked) Color.Unspecified else Color.Gray.copy(0.3f)
                    )
                    Text(
                        text       = achievement.title,
                        fontSize   = 10.sp,
                        fontWeight = if (achievement.isUnlocked) FontWeight.ExtraBold else FontWeight.Normal,
                        color      = if (achievement.isUnlocked) KidsTextPrimary else KidsTextDisabled,
                        textAlign  = TextAlign.Center
                    )
                    if (achievement.isUnlocked) Text("✓", fontSize = 12.sp, color = KidsGreen, fontWeight = FontWeight.Black)
                }
            }
            if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(10.dp))
    }
}
