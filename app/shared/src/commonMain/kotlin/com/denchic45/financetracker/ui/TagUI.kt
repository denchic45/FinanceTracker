package com.denchic45.financetracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.TagItem
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.tag
import org.jetbrains.compose.resources.painterResource

@Composable
fun TagListItem(
    modifier: Modifier = Modifier,
    tag: TagItem,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.tag),
                contentDescription = null
            )
        },
        headlineContent = {
            Text(text = tag.name)
        }
    )
}

@Composable
fun RemoveTagConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Tag") },
        text = { Text("Are you sure you want to delete this tag? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}