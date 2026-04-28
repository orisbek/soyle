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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soyle.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegister    : () -> Unit,
    viewModel     : AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Логотип ───────────────────────────────────────────────────
            Text(
                text          = "söyle.",
                fontWeight    = FontWeight.Bold,
                fontSize      = 40.sp,
                color         = SoyleTextPrimary,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text     = "войди в аккаунт",
                fontSize = 15.sp,
                color    = SoyleTextSecondary
            )

            Spacer(Modifier.height(48.dp))

            // ── Email ─────────────────────────────────────────────────────
            SoyleTextField(
                value       = email,
                onValueChange = { email = it },
                placeholder = "Email",
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
                value       = password,
                onValueChange = { password = it },
                placeholder = "Пароль",
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
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(email, password)
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

            Spacer(Modifier.height(24.dp))

            // ── Кнопка входа ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (email.isNotBlank() && password.isNotBlank() && !state.isLoading)
                            SoyleButtonPrimary else SoyleSurface
                    )
                    .clickable(enabled = email.isNotBlank() && password.isNotBlank() && !state.isLoading) {
                        focusManager.clearFocus()
                        viewModel.login(email, password)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(22.dp),
                        color     = SoyleButtonPrimaryText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Войти",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        color      = if (email.isNotBlank() && password.isNotBlank())
                            SoyleButtonPrimaryText else SoyleTextMuted
                    )
                }
            }
        }

        // ── Ссылка на регистрацию ─────────────────────────────────────────
        Row(
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Нет аккаунта?", fontSize = 14.sp, color = SoyleTextSecondary)
            Text(
                text       = "Зарегистрироваться",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = SoyleAccent,
                modifier   = Modifier.clickable(onClick = onRegister)
            )
        }
    }
}

// ── Общий компонент текстового поля ──────────────────────────────────────────

@Composable
fun SoyleTextField(
    value               : String,
    onValueChange       : (String) -> Unit,
    placeholder         : String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon        : @Composable (() -> Unit)? = null,
    keyboardOptions     : KeyboardOptions = KeyboardOptions.Default,
    keyboardActions     : KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        placeholder    = { Text(placeholder, color = SoyleTextMuted, fontSize = 15.sp) },
        visualTransformation = visualTransformation,
        trailingIcon   = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine     = true,
        shape          = RoundedCornerShape(14.dp),
        colors         = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = SoyleSurface,
            unfocusedContainerColor = SoyleSurface,
            focusedBorderColor      = SoyleAccent,
            unfocusedBorderColor    = SoyleBorder,
            focusedTextColor        = SoyleTextPrimary,
            unfocusedTextColor      = SoyleTextPrimary,
            cursorColor             = SoyleAccent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
