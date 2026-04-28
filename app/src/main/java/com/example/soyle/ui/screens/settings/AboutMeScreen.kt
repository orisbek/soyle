package com.example.soyle.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun AboutMeScreen(
    onBack    : () -> Unit,
    viewModel : SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val settings = state.settings

    var selectedGoal     by remember(settings.goal)     { mutableStateOf(settings.goal) }
    var selectedAgeGroup by remember(settings.ageGroup) { mutableStateOf(settings.ageGroup) }
    var notes            by remember(settings.notes)    { mutableStateOf(settings.notes) }

    LaunchedEffect(state.savedOk) {
        if (state.savedOk) { viewModel.clearSavedOk(); onBack() }
    }

    val goals = listOf(
        "Поставить звук «Р»", "Поставить звук «Л»",
        "Исправить шипящие", "Развить общую речь", "Что-то другое"
    )
    val ageGroups = listOf("3–4 года", "5–6 лет", "7–9 лет", "10–12 лет", "13+ лет")

    Column(
        modifier = Modifier.fillMaxSize().background(SoyleBg)
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("‹", fontSize = 28.sp, color = SoyleTextSecondary,
                modifier = Modifier.clickable(onClick = onBack))
            Text(AppLanguage.aboutMe, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = SoyleTextPrimary, letterSpacing = (-0.5).sp)
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

            // ── Цель ──────────────────────────────────────────────────────
            Text("ЦЕЛЬ", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                goals.forEach { goal ->
                    SelectableRow(
                        label      = goal,
                        isSelected = selectedGoal == goal,
                        onClick    = { selectedGoal = if (selectedGoal == goal) "" else goal }
                    )
                }
            }

            // ── Возраст ───────────────────────────────────────────────────
            Text("ВОЗРАСТ", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ageGroups.forEach { age ->
                    SelectableRow(
                        label      = age,
                        isSelected = selectedAgeGroup == age,
                        onClick    = { selectedAgeGroup = if (selectedAgeGroup == age) "" else age }
                    )
                }
            }

            // ── Заметки ───────────────────────────────────────────────────
            Text("ЗАМЕТКИ О СЕБЕ", fontSize = 11.sp, color = SoyleTextMuted, letterSpacing = 1.sp)
            OutlinedTextField(
                value         = notes,
                onValueChange = { notes = it },
                placeholder   = { Text("Что-то важное о прогрессе...", color = SoyleTextMuted, fontSize = 14.sp) },
                modifier      = Modifier.fillMaxWidth().height(120.dp),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = SoyleSurface,
                    unfocusedContainerColor = SoyleSurface,
                    focusedBorderColor      = SoyleAccent,
                    unfocusedBorderColor    = SoyleBorder,
                    focusedTextColor        = SoyleTextPrimary,
                    unfocusedTextColor      = SoyleTextPrimary,
                    cursorColor             = SoyleAccent
                )
            )

            state.error?.let { Text(it, fontSize = 13.sp, color = Color(0xFFFF6B6B)) }

            // ── Кнопка сохранить ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SoyleButtonPrimary)
                    .clickable(enabled = !state.isSaving) {
                        viewModel.saveAboutMe(selectedGoal, selectedAgeGroup, notes.trim())
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp),
                        color = SoyleButtonPrimaryText, strokeWidth = 2.dp)
                } else {
                    Text(AppLanguage.save, fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp, color = SoyleButtonPrimaryText)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SelectableRow(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SoyleAccentSoft else SoyleSurface)
            .border(1.dp, if (isSelected) SoyleAccent else SoyleBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, color = SoyleTextPrimary)
        if (isSelected) Text("✓", fontSize = 14.sp, color = SoyleAccent, fontWeight = FontWeight.Bold)
    }
}
