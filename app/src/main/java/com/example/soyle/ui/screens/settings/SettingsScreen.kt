package com.example.soyle.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
fun SettingsScreen(
    onBack      : () -> Unit,
    viewModel   : SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val settings = state.settings
    val isDark = AppTheme.isDark

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
            Text(
                text     = "‹",
                fontSize = 28.sp,
                color    = SoyleTextSecondary,
                modifier = Modifier.clickable(onClick = onBack)
            )
            Text(
                text          = AppLanguage.settings,
                fontWeight    = FontWeight.Bold,
                fontSize      = 20.sp,
                color         = SoyleTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.size(28.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Тема ──────────────────────────────────────────────────────
            SectionTitle(AppLanguage.theme)

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeCard(
                    label     = AppLanguage.darkMode,
                    emoji     = "🌙",
                    isSelected = isDark,
                    bgColor   = Color(0xFF111111),
                    textColor = Color(0xFFFFFFFF),
                    modifier  = Modifier.weight(1f)
                ) { viewModel.setTheme(true) }

                ThemeCard(
                    label     = AppLanguage.lightMode,
                    emoji     = "☀️",
                    isSelected = !isDark,
                    bgColor   = Color(0xFFF5F5F5),
                    textColor = Color(0xFF111111),
                    modifier  = Modifier.weight(1f)
                ) { viewModel.setTheme(false) }
            }

            // ── Язык ──────────────────────────────────────────────────────
            SectionTitle(AppLanguage.language)

            val langs = listOf(
                Triple("ru", "🇷🇺", "Русский"),
                Triple("kk", "🇰🇿", "Қазақша"),
                Triple("en", "🇬🇧", "English")
            )

            langs.forEach { (code, flag, name) ->
                val selected = AppLanguage.code == code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) SoyleAccentSoft else SoyleSurface)
                        .border(
                            1.dp,
                            if (selected) SoyleAccent else SoyleBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.setLanguage(code) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(flag, fontSize = 22.sp)
                        Text(name, fontSize = 15.sp, color = SoyleTextPrimary, fontWeight = FontWeight.Medium)
                    }
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(SoyleAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ThemeCard(
    label     : String,
    emoji     : String,
    isSelected: Boolean,
    bgColor   : Color,
    textColor : Color,
    modifier  : Modifier = Modifier,
    onClick   : () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width  = if (isSelected) 2.dp else 1.dp,
                color  = if (isSelected) SoyleAccent else SoyleBorder,
                shape  = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(emoji, fontSize = 28.sp)
            Text(label, fontSize = 13.sp, color = textColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        color         = SoyleTextMuted,
        letterSpacing = 1.sp,
        fontWeight    = FontWeight.Medium
    )
}
