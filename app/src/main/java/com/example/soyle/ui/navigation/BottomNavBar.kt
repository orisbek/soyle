package com.example.soyle.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.theme.*

// ── Нижняя навигация — тёмный стиль (как в Stoic) ────────────────────────────

data class NavItem(
    val route  : String,
    val icon   : String,
    val label  : String
)

@Composable
fun SoyleBottomNav(
    currentRoute : String,
    onNavigate   : (String) -> Unit
) {
    val items = listOf(
        NavItem(Screen.Home.route,    "🏠", "Главная"),
        NavItem(Screen.Games.route,   "🎮", "Игры"),
        NavItem(Screen.Profile.route, "◎",  "Профиль")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SoyleBg)
            .border(
                width = 1.dp,
                color = SoyleBorder,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val textColor by animateColorAsState(
                    if (isSelected) SoyleTextPrimary else SoyleTextMuted,
                    label = "navColor${item.route}"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(item.route) }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Иконка
                    Text(
                        text     = item.icon,
                        fontSize = if (isSelected) 22.sp else 20.sp,
                        color    = textColor
                    )
                    // Лейбл
                    Text(
                        text       = item.label,
                        fontSize   = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = textColor
                    )
                }
            }
        }

        // Плавающая кнопка "+" по центру (как в Stoic Home)
        // — для быстрого начала упражнения
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(SoyleButtonPrimary)
                .border(3.dp, SoyleBg, CircleShape)
                .clickable { onNavigate(Screen.CheckIn.route) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "+",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Normal,
                color      = SoyleButtonPrimaryText
            )
        }
    }
}