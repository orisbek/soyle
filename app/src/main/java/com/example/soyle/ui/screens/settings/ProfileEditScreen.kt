package com.example.soyle.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.screens.auth.SoyleTextField
import com.example.soyle.ui.theme.*

private val AVATARS = listOf("🧑","👦","👧","🧒","👨","👩","🧔","👶","🦊","🐻","🐼","🦁","🐯","🦋","🌟","🚀","🎯","🔥")

@Composable
fun ProfileEditScreen(
    onBack    : () -> Unit,
    viewModel : SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val settings = state.settings

    var name   by remember(settings.displayName) { mutableStateOf(settings.displayName) }
    var avatar by remember(settings.avatarEmoji)  { mutableStateOf(settings.avatarEmoji) }

    // После сохранения — закрываем
    LaunchedEffect(state.savedOk) {
        if (state.savedOk) { viewModel.clearSavedOk(); onBack() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // ── Шапка ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("‹", fontSize = 28.sp, color = SoyleTextSecondary,
                modifier = Modifier.clickable(onClick = onBack))
            Text(AppLanguage.editProfile, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = SoyleTextPrimary, letterSpacing = (-0.5).sp)
            Spacer(Modifier.size(28.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Текущий аватар ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SoyleSurface)
                    .border(2.dp, SoyleAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(avatar, fontSize = 44.sp)
            }

            // ── Выбор аватара ─────────────────────────────────────────────
            Text("Выбери аватар", fontSize = 13.sp, color = SoyleTextMuted)

            // Сетка эмодзи
            AVATARS.chunked(6).forEach { row ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { emoji ->
                        val sel = emoji == avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (sel) SoyleAccentSoft else SoyleSurface)
                                .border(
                                    if (sel) 2.dp else 1.dp,
                                    if (sel) SoyleAccent else SoyleBorder,
                                    CircleShape
                                )
                                .clickable { avatar = emoji },
                            contentAlignment = Alignment.Center
                        ) { Text(emoji, fontSize = 22.sp) }
                    }
                }
            }

            // ── Поле имени ────────────────────────────────────────────────
            SoyleTextField(
                value           = name,
                onValueChange   = { name = it },
                placeholder     = "Твоё имя",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction      = ImeAction.Done
                )
            )

            // ── Ошибка ────────────────────────────────────────────────────
            state.error?.let {
                Text(it, fontSize = 13.sp, color = Color(0xFFFF6B6B))
            }

            Spacer(Modifier.height(8.dp))

            // ── Кнопка сохранить ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (name.isNotBlank()) SoyleButtonPrimary else SoyleSurface)
                    .clickable(enabled = name.isNotBlank() && !state.isSaving) {
                        viewModel.saveProfile(name.trim(), avatar)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = SoyleButtonPrimaryText, strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        AppLanguage.save,
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        color = if (name.isNotBlank()) SoyleButtonPrimaryText else SoyleTextMuted
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
