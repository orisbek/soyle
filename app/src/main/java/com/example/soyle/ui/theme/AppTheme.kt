package com.example.soyle.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ── Глобальное состояние темы ─────────────────────────────────────────────────

object AppTheme {
    var isDark by mutableStateOf(true)
}

// ── Набор цветов для одной темы ───────────────────────────────────────────────

private data class ColorSet(
    val bg          : Color,
    val surface     : Color,
    val surface2    : Color,
    val surface3    : Color,
    val border      : Color,
    val borderLight : Color,
    val textPrimary : Color,
    val textSec     : Color,
    val textMuted   : Color,
    val textDisabled: Color,
    val btnPrimary  : Color,
    val btnText     : Color
)

private val darkColors = ColorSet(
    bg           = Color(0xFF000000),
    surface      = Color(0xFF111111),
    surface2     = Color(0xFF1A1A1A),
    surface3     = Color(0xFF242424),
    border       = Color(0xFF2A2A2A),
    borderLight  = Color(0xFF3A3A3A),
    textPrimary  = Color(0xFFFFFFFF),
    textSec      = Color(0xFF888888),
    textMuted    = Color(0xFF555555),
    textDisabled = Color(0xFF333333),
    btnPrimary   = Color(0xFFE8E8E8),
    btnText      = Color(0xFF111111)
)

private val lightColors = ColorSet(
    bg           = Color(0xFFF5F5F5),
    surface      = Color(0xFFFFFFFF),
    surface2     = Color(0xFFEEEEEE),
    surface3     = Color(0xFFE5E5E5),
    border       = Color(0xFFDDDDDD),
    borderLight  = Color(0xFFE8E8E8),
    textPrimary  = Color(0xFF111111),
    textSec      = Color(0xFF555555),
    textMuted    = Color(0xFF888888),
    textDisabled = Color(0xFFBBBBBB),
    btnPrimary   = Color(0xFF111111),
    btnText      = Color(0xFFFFFFFF)
)

// ── Применяет тему — Compose перерисует весь UI автоматически ─────────────────

fun applyTheme(isDark: Boolean) {
    AppTheme.isDark = isDark
    val c = if (isDark) darkColors else lightColors

    SoyleBg               = c.bg
    SoyleSurface          = c.surface
    SoyleSurface2         = c.surface2
    SoyleSurface3         = c.surface3
    SoyleBorder           = c.border
    SoyleBorderLight      = c.borderLight
    SoyleTextPrimary      = c.textPrimary
    SoyleTextSecondary    = c.textSec
    SoyleTextMuted        = c.textMuted
    SoyleTextDisabled     = c.textDisabled
    SoyleButtonPrimary    = c.btnPrimary
    SoyleButtonPrimaryText = c.btnText
}

// ── Язык ──────────────────────────────────────────────────────────────────────

object AppLanguage {
    var code by mutableStateOf("ru")   // "ru" | "kk" | "en"

    val home          get() = when (code) { "kk" -> "Басты";           "en" -> "Home";          else -> "Главная"         }
    val games         get() = when (code) { "kk" -> "Ойындар";         "en" -> "Games";         else -> "Игры"            }
    val profile       get() = when (code) { "kk" -> "Профиль";         "en" -> "Profile";       else -> "Профиль"         }
    val settings      get() = when (code) { "kk" -> "Баптаулар";       "en" -> "Settings";      else -> "Настройки"       }
    val save          get() = when (code) { "kk" -> "Сақтау";          "en" -> "Save";          else -> "Сохранить"       }
    val cancel        get() = when (code) { "kk" -> "Болдырмау";       "en" -> "Cancel";        else -> "Отмена"          }
    val theme         get() = when (code) { "kk" -> "Тема";            "en" -> "Theme";         else -> "Тема"            }
    val language      get() = when (code) { "kk" -> "Тіл";             "en" -> "Language";      else -> "Язык"            }
    val darkMode      get() = when (code) { "kk" -> "Қараңғы режим";   "en" -> "Dark mode";     else -> "Тёмная тема"     }
    val lightMode     get() = when (code) { "kk" -> "Жарық режим";     "en" -> "Light mode";    else -> "Светлая тема"    }
    val editProfile   get() = when (code) { "kk" -> "Профильді өңдеу"; "en" -> "Edit profile";  else -> "Изменить профиль"}
    val aboutMe       get() = when (code) { "kk" -> "Өзім туралы";     "en" -> "About me";      else -> "О себе"          }
    val notifications get() = when (code) { "kk" -> "Хабарландырулар"; "en" -> "Notifications"; else -> "Уведомления"     }
}
