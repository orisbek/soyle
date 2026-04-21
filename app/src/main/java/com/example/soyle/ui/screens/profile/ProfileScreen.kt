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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenProgress: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val profile = uiState.profile
    val level         = profile?.level ?: 1
    val totalXp       = profile?.totalXp ?: 0
    val xpInLevel     = totalXp % 500
    val currentStreak = 0 
    val longestStreak = 0

    val animatedXp by animateFloatAsState(
        targetValue   = xpInLevel / 500f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label         = "xpBar"
    )

    Scaffold(
        containerColor = KidsBg,
        bottomBar = {
            KidsFloatingBottomBar(
                currentRoute = "profile",
                onHome     = onOpenHome,
                onProgress = onOpenProgress,
                onProfile  = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // ── Шапка ─────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(KidsOrange, KidsPink)))
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
                        text       = "👦 Профиль",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = KidsMint)
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(4.dp))

                    // ── Карточка аватара ───────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .border(2.dp, KidsBorder, RoundedCornerShape(24.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(KidsMintLight, KidsBlueLight)))
                                .border(4.dp, KidsMint, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🦜", fontSize = 48.sp)
                        }
                        Text(profile?.name ?: "Малыш", fontWeight = FontWeight.Black, fontSize = 22.sp, color = KidsTextPrimary)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(KidsPurple.copy(0.15f))
                                .border(2.dp, KidsPurple, RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text       = "✦ Уровень $level",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = KidsPurple
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("$xpInLevel XP", fontSize = 12.sp, color = KidsTextSecondary, fontWeight = FontWeight.Bold)
                                Text("500 XP", fontSize = 12.sp, color = KidsTextSecondary, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color(0xFFEEEEEE))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedXp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Brush.horizontalGradient(listOf(KidsPurple, KidsBlue)))
                                )
                            }
                        }
                    }

                    // ── Серия ─────────────────────────────────────────────────────
                    KidsSectionTitle("🔥 Серия дней")
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KidsStreakTile("🔥", "$currentStreak",             "Текущая",  KidsOrange, Modifier.weight(1f))
                        KidsStreakTile("🏆", "$longestStreak",             "Рекорд",   KidsYellow, Modifier.weight(1f))
                        KidsStreakTile("⭐", "${7 - currentStreak % 7}",   "До награды", KidsPurple, Modifier.weight(1f))
                    }

                    // ── Маскоты ───────────────────────────────────────────────────
                    KidsSectionTitle("🦜 Маскоты")
                    KidsMascotGallery(currentLevel = level)

                    // ── Настройки ─────────────────────────────────────────────────
                    KidsSectionTitle("⚙️ Настройки")
                    KidsSettingsCard()

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun KidsSectionTitle(title: String) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = KidsTextPrimary)
}

@Composable
private fun KidsStreakTile(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(0.1f))
            .border(2.dp, color.copy(0.35f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 26.sp)
        Text(value,  fontWeight = FontWeight.Black, fontSize = 22.sp, color = color)
        Text(label,  fontSize = 10.sp, color = KidsTextSecondary, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun KidsMascotGallery(currentLevel: Int) {
    val mascots = listOf(
        Triple("🦜", "Рыжик",  1),
        Triple("🐻", "Мишка",  3),
        Triple("🐸", "Кваки",  5),
        Triple("🦊", "Лиса",   8),
        Triple("🐉", "Дракон", 15),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(2.dp, KidsBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        mascots.forEach { (emoji, name, req) ->
            val unlocked = currentLevel >= req
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            if (unlocked) KidsMintLight else Color(0xFFF0F0F0)
                        )
                        .then(
                            if (unlocked) Modifier.border(3.dp, KidsMint, CircleShape) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = emoji,
                        fontSize = 26.sp,
                        color    = if (unlocked) Color.Unspecified else Color.Gray.copy(0.3f)
                    )
                }
                Text(
                    text       = if (unlocked) name else "Ур.$req",
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = if (unlocked) KidsTextPrimary else KidsTextDisabled,
                    textAlign  = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun KidsSettingsCard() {
    var notif by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(2.dp, KidsBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SettingsRow(emoji = "🔔", title = "Напоминания", checked = notif) { notif = it }
        HorizontalDivider(color = KidsBorder, thickness = 1.dp)
        SettingsRow(emoji = "🔊", title = "Звуки",       checked = sound) { sound = it }
    }
}

@Composable
private fun SettingsRow(emoji: String, title: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = KidsTextPrimary)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor  = KidsMint,
                checkedTrackColor  = KidsMintLight,
                uncheckedThumbColor = Color(0xFFCCCCCC),
                uncheckedTrackColor = Color(0xFFEEEEEE)
            )
        )
    }
}
