package com.example.soyle.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soyle.ui.components.DuoLevelBadge
import com.example.soyle.ui.components.DuoProgressBar
import com.example.soyle.ui.components.DuoSectionHeader
import com.example.soyle.ui.theme.*

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val level = 4
    val totalXp = 1820
    val xpInLevel = totalXp % 500

    Scaffold(
        containerColor = DuoBg,
        topBar = {
            TopAppBar(
                title = { Text("Профиль", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DuoWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Column(
                modifier = Modifier.fillMaxWidth().border(2.dp, DuoBorder, RoundedCornerShape(20.dp)).background(DuoWhite, RoundedCornerShape(20.dp)).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.size(80.dp).background(DuoGreenLight, CircleShape), contentAlignment = Alignment.Center) {
                    Text("S", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = DuoGreen)
                }
                Text("Пользователь", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = DuoTextPrimary)
                DuoLevelBadge(level)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$xpInLevel XP", fontSize = 12.sp, color = DuoTextSecondary)
                        Text("500 XP", fontSize = 12.sp, color = DuoTextSecondary)
                    }
                    Spacer(Modifier.height(4.dp))
                    DuoProgressBar(progress = xpInLevel / 500f)
                }
            }

            DuoSectionHeader("Настройки")
            SettingsCard()
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SettingsCard() {
    var notif by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth().border(2.dp, DuoBorder, RoundedCornerShape(16.dp)).background(DuoWhite, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Напоминания", fontWeight = FontWeight.SemiBold, color = DuoTextPrimary)
            Switch(checked = notif, onCheckedChange = { notif = it }, colors = SwitchDefaults.colors(checkedThumbColor = DuoGreen, checkedTrackColor = DuoGreenLight))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Звуки", fontWeight = FontWeight.SemiBold, color = DuoTextPrimary)
            Switch(checked = sound, onCheckedChange = { sound = it }, colors = SwitchDefaults.colors(checkedThumbColor = DuoGreen, checkedTrackColor = DuoGreenLight))
        }
    }
}
