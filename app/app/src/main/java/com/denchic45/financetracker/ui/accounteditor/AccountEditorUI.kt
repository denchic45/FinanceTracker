package com.denchic45.financetracker.ui.accounteditor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.api.account.model.AccountType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditorDialog(
    accountId: UUID?,
    onFinish: () -> Unit
) {
    val viewModel = koinViewModel<AccountEditorViewModel> {
        parametersOf(accountId)
    }
    val state = viewModel.state

    var showRemoveConfirmation by remember { mutableStateOf(false) }
    AlertDialog(
        title = { Text(if (accountId != null) "Редактировать аккаунт" else "Создать аккаунт") },
        text = {
            var accountTypesExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = accountTypesExpanded, onExpandedChange = { accountTypesExpanded = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable),
                    value = state.type.displayName,
                    onValueChange = { },
                    readOnly = true,
                    singleLine = true,
                    label = { Text("Тип аккаунта") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = accountTypesExpanded,
                            modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable),
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    supportingText = {})
                DropdownMenu(
                    expanded = accountTypesExpanded,
                    onDismissRequest = { accountTypesExpanded = false }) {
                    AccountType.entries.forEach {
                        DropdownMenuItem(text = { Text(it.displayName) }, onClick = {
                            accountTypesExpanded = false
                            viewModel.onAccountTypeChange(it)
                        })
                    }
                }
            }
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onAccountNameChanged,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            FilledTonalButton(onClick = viewModel::onSaveClick) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = {
                if (viewModel.hasChanges()) showRemoveConfirmation = true
                else onFinish()
            }) { Text("Отмена") }
        },
        onDismissRequest = {
            if (viewModel.hasChanges()) showRemoveConfirmation = true
            else onFinish()
        }
    )

    BackHandler(viewModel.hasChanges()) {
        showRemoveConfirmation = true
    }

    if (showRemoveConfirmation) {
        AlertDialog(
            onDismissRequest = { showRemoveConfirmation = false },
            title = { Text("Вы уверены?") },
            text = { Text("При закрытии изменения не будут сохранены") },
            confirmButton = {
                TextButton(onClick = {
                    showRemoveConfirmation = false
                    onFinish()
                }) { Text("Закрыть") }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveConfirmation = false }) {
                    Text("Отменить")
                }
            })
    }
}