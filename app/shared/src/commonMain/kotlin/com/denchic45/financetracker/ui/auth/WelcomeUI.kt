package com.denchic45.financetracker.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.auth_sign_in_btn
import financetracker_app.shared.generated.resources.auth_sign_up_btn
import financetracker_app.shared.generated.resources.auth_welcome
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeScreen(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(Res.string.auth_welcome),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onSignInClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.auth_sign_in_btn))
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.auth_sign_up_btn))
        }
    }
}