package com.example.soyle.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soyle.ui.components.DuoButton
import com.example.soyle.ui.components.DuoOutlineButton
import com.example.soyle.ui.theme.DuoBg
import com.example.soyle.ui.theme.DuoRed

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Soyle", fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Пароль") },
            singleLine = true
        )

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = DuoRed)
        }

        Spacer(Modifier.height(18.dp))

        DuoButton(
            text = if (state.isRegisterMode) "Зарегистрироваться" else "Войти",
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            onClick = viewModel::submit
        )

        Spacer(Modifier.height(10.dp))

        DuoOutlineButton(
            text = if (state.isRegisterMode) "Уже есть аккаунт" else "Создать аккаунт",
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            onClick = viewModel::toggleMode
        )

        if (state.isLoading) {
            Spacer(Modifier.height(14.dp))
            CircularProgressIndicator()
        }
    }
}
