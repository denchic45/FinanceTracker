package com.denchic45.financetracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.CategoryItem
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource

@Composable
fun CategoryListItem(
    modifier: Modifier = Modifier,
    category: CategoryItem,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                painter = painterResource(Res.allDrawableResources.getValue(category.iconName)),
                contentDescription = category.name,
                modifier = Modifier.size(40.dp)
            )
        },
        headlineContent = { Text(text = category.name) }
    )
}

@Composable
fun RemoveCategoryConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Category") },
        text = { Text("Are you sure you want to delete this category? This action cannot be undone.") },
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