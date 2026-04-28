package com.example.soyle.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.theme.*

data class NavItem(
    val route : String,
    val icon  : ImageVector,
    val label : String
)

@Composable
fun SoyleBottomNav(
    currentRoute : String,
    onNavigate   : (String) -> Unit
) {
    val items = listOf(
        NavItem(Screen.Home.route,    Icons.Outlined.Home,          AppLanguage.home),
        NavItem(Screen.Games.route,   Icons.Outlined.SportsEsports, AppLanguage.games),
        NavItem(Screen.Profile.route, Icons.Outlined.Person,        AppLanguage.profile)
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
                val iconColor by animateColorAsState(
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
                    Icon(
                        imageVector       = item.icon,
                        contentDescription = item.label,
                        tint              = iconColor,
                        modifier          = Modifier.size(if (isSelected) 24.dp else 22.dp)
                    )
                    Text(
                        text       = item.label,
                        fontSize   = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = iconColor
                    )
                }
            }
        }
    }
}
