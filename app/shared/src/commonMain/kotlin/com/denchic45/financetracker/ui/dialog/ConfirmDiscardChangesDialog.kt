package com.denchic45.financetracker.ui.dialog

import androidx.compose.runtime.Composable
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.discard_changes_dialog_message
import financetracker_app.shared.generated.resources.discard_changes_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDiscardChangesDialog(
    show: Boolean,
    title: String = stringResource(Res.string.discard_changes_dialog_title),
    text: String = stringResource(Res.string.discard_changes_dialog_message),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmDialog(
        title = title,
        text = text,
        show = show,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}