package com.example.soyle.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.theme.*

@Composable
fun NotificationsScreen(
    onBack    : () -> Unit,
    viewModel : NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(SoyleBg)) {

        // ── Шапка ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                "‹", fontSize = 28.sp, color = SoyleTextSecondary,
                modifier = Modifier.clickable(onClick = onBack)
            )
            Text(
                AppLanguage.notifications,
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = SoyleTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.size(28.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Утреннее уведомление ──────────────────────────────────────
            Text("УТРО", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)

            NotificationTimeCard(
                label     = "Утреннее напоминание",
                enabled   = state.morningEnabled,
                hour      = state.morningHour,
                minute    = state.morningMinute,
                onToggle  = viewModel::setMorningEnabled,
                onHour    = viewModel::setMorningHour,
                onMinute  = viewModel::setMorningMinute
            )

            // ── Вечернее уведомление ──────────────────────────────────────
            Text("ВЕЧЕР", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)

            NotificationTimeCard(
                label     = "Вечернее напоминание",
                enabled   = state.eveningEnabled,
                hour      = state.eveningHour,
                minute    = state.eveningMinute,
                onToggle  = viewModel::setEveningEnabled,
                onHour    = viewModel::setEveningHour,
                onMinute  = viewModel::setEveningMinute
            )

            state.error?.let {
                Text(it, fontSize = 13.sp, color = Color(0xFFFF6B6B))
            }

            // ── Кнопка сохранить ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SoyleButtonPrimary)
                    .clickable(enabled = !state.isSaving) { viewModel.save(onBack) },
                contentAlignment = Alignment.Center
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = SoyleButtonPrimaryText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        AppLanguage.save,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        color      = SoyleButtonPrimaryText
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Карточка с переключателем и пикером времени ───────────────────────────────

@Composable
private fun NotificationTimeCard(
    label    : String,
    enabled  : Boolean,
    hour     : Int,
    minute   : Int,
    onToggle : (Boolean) -> Unit,
    onHour   : (Int) -> Unit,
    onMinute : (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SoyleSurface)
            .border(1.dp, SoyleBorder, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Строка переключатель
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 15.sp, color = SoyleTextPrimary, fontWeight = FontWeight.Medium)
            Switch(
                checked         = enabled,
                onCheckedChange = onToggle,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor   = Color.White,
                    checkedTrackColor   = SoyleAccent,
                    uncheckedThumbColor = SoyleTextMuted,
                    uncheckedTrackColor = SoyleSurface2
                )
            )
        }

        // Пикер времени (только если включён)
        if (enabled) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Часы
                TimeSpinner(
                    value    = hour,
                    max      = 23,
                    onChange = { newH -> onHour(newH.coerceIn(0, 23)) }
                )

                Text(
                    ":",
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color      = SoyleTextPrimary,
                    modifier   = Modifier.padding(horizontal = 8.dp)
                )

                // Минуты
                TimeSpinner(
                    value    = minute,
                    max      = 59,
                    onChange = { newM -> onMinute(newM.coerceIn(0, 59)) }
                )
            }
        }
    }
}

@Composable
private fun TimeSpinner(value: Int, max: Int, onChange: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // + кнопка
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SoyleSurface2)
                .border(1.dp, SoyleBorder, RoundedCornerShape(10.dp))
                .clickable { onChange((value + 1).let { if (it > max) 0 else it }) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Add,
                contentDescription = "Больше",
                tint               = SoyleTextPrimary,
                modifier           = Modifier.size(18.dp)
            )
        }

        // Значение
        Box(
            modifier = Modifier
                .width(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SoyleSurface2)
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "%02d".format(value),
                fontSize   = 30.sp,
                fontWeight = FontWeight.Bold,
                color      = SoyleTextPrimary
            )
        }

        // − кнопка
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SoyleSurface2)
                .border(1.dp, SoyleBorder, RoundedCornerShape(10.dp))
                .clickable { onChange((value - 1).let { if (it < 0) max else it }) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Remove,
                contentDescription = "Меньше",
                tint               = SoyleTextPrimary,
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}
