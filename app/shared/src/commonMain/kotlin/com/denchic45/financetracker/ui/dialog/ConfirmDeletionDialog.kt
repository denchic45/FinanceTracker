package com.denchic45.financetracker.ui.dialog

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.common_delete
import financetracker_app.shared.generated.resources.trash
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDeletionDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = title,
        text = text,
        icon = { Icon(painterResource(Res.drawable.trash), null) },
        confirmText = stringResource(Res.string.common_delete),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        modifier = Modifier.widthIn(max = 412.dp)
    )
}