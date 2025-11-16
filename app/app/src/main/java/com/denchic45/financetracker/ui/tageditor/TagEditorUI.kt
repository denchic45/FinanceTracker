package com.denchic45.financetracker.ui.tageditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagEditorDialog(
    tagId: Long?,
    viewModel: TagEditorViewModel = koinViewModel { parametersOf(tagId) }
) {
    val state = viewModel.state

    AlertDialog(
        onDismissRequest = viewModel::onDismissClick,
        title = { Text(if (tagId == null) "New Tag" else "Edit Tag") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Name") },
                    isError = state.nameMessage != null,
                    supportingText = { state.nameMessage?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::onSaveClick) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::onDismissClick) {
                Text("Cancel")
            }
        }
    )
}
