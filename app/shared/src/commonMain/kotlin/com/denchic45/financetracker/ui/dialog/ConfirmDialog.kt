package com.denchic45.financetracker.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.common_cancel
import financetracker_app.shared.generated.resources.common_ok
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDialog(
    show: Boolean,
    title: String,
    text: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(Res.string.common_ok),
    dismissText: String = stringResource(Res.string.common_cancel),
    icon: (@Composable () -> Unit)? = null
) {
    if (show)
        ConfirmDialog(
            title = title,
            text = text,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            confirmText = confirmText,
            dismissText = dismissText,
            icon = icon
        )
}

@Composable
fun ConfirmDialog(
    modifier: Modifier = Modifier,
    title: String,
    text: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(Res.string.common_ok),
    dismissText: String = stringResource(Res.string.common_cancel),
    icon: (@Composable () -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = text?.let { { Text(it) } },
        confirmButton = { FilledTonalButton(onClick = onConfirm) { Text(confirmText) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissText) } },
        icon = icon,
        modifier = modifier
    )
}