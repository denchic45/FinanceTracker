package com.denchic45.financetracker.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.auth_email_field
import financetracker_app.shared.generated.resources.auth_firstname_field
import financetracker_app.shared.generated.resources.auth_lastname_field
import financetracker_app.shared.generated.resources.auth_password_field
import financetracker_app.shared.generated.resources.auth_repeat_password_field
import financetracker_app.shared.generated.resources.auth_sign_up
import financetracker_app.shared.generated.resources.auth_sign_up_btn
import financetracker_app.shared.generated.resources.common_back
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(onNavigateBack: () -> Unit) {
    val viewModel = koinViewModel<SignUpViewModel>()
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(Res.string.auth_sign_up),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(32.dp))

        AuthTextField(
            value = state.firstName,
            onValueChange = { state.firstName = it },
            label = { Text(stringResource(Res.string.auth_firstname_field)) },
            errorMessage = state.firstNameMessage,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        )
        Spacer(Modifier.height(16.dp))
        AuthTextField(
            value = state.lastName,
            onValueChange = { state.lastName = it },
            label = { Text(stringResource(Res.string.auth_lastname_field)) },
            errorMessage = state.lastNameMessage,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        )
        Spacer(Modifier.height(16.dp))
        AuthTextField(
            value = state.email,
            onValueChange = { state.email = it },
            label = { Text(stringResource(Res.string.auth_email_field)) },
            errorMessage = state.emailMessage,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
        Spacer(Modifier.height(16.dp))
        PasswordField(
            value = state.password,
            onValueChange = { state.password = it },
            label = { Text(stringResource(Res.string.auth_password_field)) },
            errorMessage = state.passwordMessage,
            imeAction = ImeAction.Next
        )
        Spacer(Modifier.height(16.dp))
        PasswordField(
            value = state.retryPassword,
            onValueChange = { state.retryPassword = it },
            label = { Text(stringResource(Res.string.auth_repeat_password_field)) },
            errorMessage = state.retryPasswordMessage,
            imeAction = ImeAction.Done
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (viewModel.validate()) {
                    viewModel.onSignUpClick()
                }
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(Res.string.auth_sign_up_btn))
            }
        }

        state.errorMessage?.let { message ->
            Spacer(Modifier.height(4.dp))
            Text(
                text = message,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }


        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text(stringResource(Res.string.common_back))
        }
    }
}