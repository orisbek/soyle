package com.example.soyle.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack           : () -> Unit,
    viewModel        : AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onRegisterSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
            .padding(horizontal = 24.dp)
    ) {
        // ── Кнопка назад ─────────────────────────────────────────────────
        Text(
            text     = "‹",
            fontSize = 28.sp,
            color    = SoyleTextSecondary,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp)
                .clickable(onClick = onBack)
        )

        Column(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text          = "söyle.",
                fontWeight    = FontWeight.Bold,
                fontSize      = 40.sp,
                color         = SoyleTextPrimary,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text     = "создай аккаунт — всё с нуля",
                fontSize = 15.sp,
                color    = SoyleTextSecondary
            )

            Spacer(Modifier.height(48.dp))

            // ── Имя ───────────────────────────────────────────────────────
            SoyleTextField(
                value         = name,
                onValueChange = { name = it },
                placeholder   = "Имя",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction      = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(12.dp))

            // ── Email ─────────────────────────────────────────────────────
            SoyleTextField(
                value         = email,
                onValueChange = { email = it },
                placeholder   = "Email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(12.dp))

            // ── Пароль ────────────────────────────────────────────────────
            SoyleTextField(
                value         = password,
                onValueChange = { password = it },
                placeholder   = "Пароль (мин. 6 символов)",
                visualTransformation = if (showPassword) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text     = if (showPassword) "скрыть" else "показать",
                        fontSize = 12.sp,
                        color    = SoyleTextMuted,
                        modifier = Modifier.clickable { showPassword = !showPassword }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (canRegister(name, email, password)) {
                            viewModel.register(email, password, name)
                        }
                    }
                )
            )

            // ── Ошибка ────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.error != null,
                enter   = fadeIn(),
                exit    = fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2D1A1A))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text      = state.error ?: "",
                            fontSize  = 13.sp,
                            color     = Color(0xFFFF6B6B),
                            textAlign = TextAlign.Center,
                            modifier  = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Кнопка регистрации ────────────────────────────────────────
            val canSubmit = canRegister(name, email, password) && !state.isLoading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (canSubmit) SoyleButtonPrimary else SoyleSurface)
                    .clickable(enabled = canSubmit) {
                        focusManager.clearFocus()
                        viewModel.register(email, password, name)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = SoyleButtonPrimaryText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Создать аккаунт",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        color      = if (canSubmit) SoyleButtonPrimaryText else SoyleTextMuted
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text      = "Регистрируясь, ты получаешь отдельный\nаккаунт с нулевым прогрессом.",
                fontSize  = 12.sp,
                color     = SoyleTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }

        // ── Ссылка на вход ────────────────────────────────────────────────
        Row(
            modifier              = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Уже есть аккаунт?", fontSize = 14.sp, color = SoyleTextSecondary)
            Text(
                text       = "Войти",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = SoyleAccent,
                modifier   = Modifier.clickable(onClick = onBack)
            )
        }
    }
}

private fun canRegister(name: String, email: String, password: String) =
    name.isNotBlank() && email.isNotBlank() && password.length >= 6
