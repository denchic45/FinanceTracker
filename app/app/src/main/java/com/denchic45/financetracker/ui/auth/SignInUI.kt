package com.denchic45.financetracker.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(onNavigateBack: () -> Unit) {
    val viewModel = koinViewModel<SignInViewModel>()
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вход", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        AuthTextField(
            value = state.email,
            onValueChange = { state.email = it },
            label = { Text("Почта") },
            errorMessage = state.emailMessage,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = state.password,
            onValueChange = { state.password = it },
            label = { Text("Пароль") },
            errorMessage = state.passwordMessage,
            imeAction = ImeAction.Done
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (viewModel.validate()) {
                    viewModel.onSignInClick()
                }
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Войти")
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Назад")
        }
    }
}