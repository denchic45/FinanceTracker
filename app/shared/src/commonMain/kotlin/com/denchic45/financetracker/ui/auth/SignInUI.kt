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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.api.error.WrongEmail
import com.denchic45.financetracker.api.error.WrongPassword
import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.NoConnection
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.auth_email_field
import financetracker_app.shared.generated.resources.auth_error_wrong_email
import financetracker_app.shared.generated.resources.auth_error_wrong_password
import financetracker_app.shared.generated.resources.auth_password_field
import financetracker_app.shared.generated.resources.auth_sign_in
import financetracker_app.shared.generated.resources.auth_sign_in_btn
import financetracker_app.shared.generated.resources.common_back
import financetracker_app.shared.generated.resources.common_error_no_connection
import financetracker_app.shared.generated.resources.common_error_unknown
import financetracker_app.shared.generated.resources.validation_email_invalid
import financetracker_app.shared.generated.resources.validation_email_required
import financetracker_app.shared.generated.resources.validation_password_required
import financetracker_app.shared.generated.resources.validation_password_too_short
import org.jetbrains.compose.resources.stringResource
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
        Text(
            stringResource(Res.string.auth_sign_in),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(32.dp))

        AuthTextField(
            value = state.email,
            onValueChange = { state.email = it },
            label = { Text(stringResource(Res.string.auth_email_field)) },
            errorMessage = if (state.showEmailError) {
                if (state.email.isEmpty()) stringResource(Res.string.validation_email_required)
                else stringResource(Res.string.validation_email_invalid)
            } else null,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = state.password,
            onValueChange = { state.password = it },
            label = { Text(stringResource(Res.string.auth_password_field)) },
            errorMessage = if (state.showPasswordError) {
                if (state.password.isEmpty()) stringResource(Res.string.validation_password_required)
                else stringResource(Res.string.validation_password_too_short)
            } else null,
            imeAction = ImeAction.Done
        )

        Spacer(Modifier.height(24.dp))

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
                Text(stringResource(Res.string.auth_sign_in_btn))
            }
        }

        state.resultError?.let { failure ->
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    when (failure) {
                        NoConnection -> Res.string.common_error_no_connection
                        is ApiFailure -> {
                            when (failure.error) {
                                is WrongEmail -> Res.string.auth_error_wrong_email
                                is WrongPassword -> Res.string.auth_error_wrong_password
                                else -> Res.string.common_error_unknown
                            }
                        }

                        else -> Res.string.common_error_unknown
                    }
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(if (state.resultError == null) 8.dp else 0.dp))

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text(stringResource(Res.string.common_back))
        }
    }
}
