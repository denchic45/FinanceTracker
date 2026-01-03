package com.denchic45.financetracker.ui.accounteditor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.domain.model.displayName
import com.denchic45.financetracker.ui.CurrencyVisualTransformation
import com.denchic45.financetracker.ui.resource.get
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.check
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditorScreen(
    accountId: UUID?,
    viewModel: AccountEditorViewModel = koinViewModel<AccountEditorViewModel> {
        parametersOf(accountId)
    }
) {
    val state = viewModel.state

    var showDiscardConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (accountId != null) "Редактировать аккаунт" else "Создать аккаунт") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.hasChanges()) showDiscardConfirmation = true
                        else viewModel.onDismissClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (state.isLoading)
                        IconButton(onClick = {}) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    else IconButton(onClick = viewModel::onSaveClick) {
                        Icon(
                            painterResource(Res.drawable.check),
                            contentDescription = "Save changes"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onAccountNameChanged,
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameMessage != null,
                supportingText = { state.nameMessage?.let { Text(it) } }
            )
            var accountTypesExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = accountTypesExpanded,
                onExpandedChange = { accountTypesExpanded = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                    value = state.type.displayName.get(),
                    onValueChange = { },
                    readOnly = true,
                    singleLine = true,
                    label = { Text("Тип аккаунта") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = accountTypesExpanded,
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    supportingText = {})
                ExposedDropdownMenu(
                    expanded = accountTypesExpanded,
                    onDismissRequest = { accountTypesExpanded = false }) {
                    AccountType.entries.forEach {
                        DropdownMenuItem(text = { Text(it.displayName.get()) }, onClick = {
                            accountTypesExpanded = false
                            viewModel.onAccountTypeChange(it)
                        })
                    }
                }
            }

            if (accountId == null)
                OutlinedTextField(
                    value = state.balance,
                    onValueChange = viewModel::onBalanceChanged,
                    label = { Text("Баланс") },
                    placeholder = { Text("10000 ₽") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.balanceMessage != null,
                    supportingText = { state.balanceMessage?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    visualTransformation = CurrencyVisualTransformation()
                )
        }
    }

    BackHandler(viewModel.hasChanges()) {
        showDiscardConfirmation = true
    }

    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text("Вы уверены?") },
            text = { Text("При закрытии изменения не будут сохранены") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardConfirmation = false
                    viewModel.onDismissClick()
                }) { Text("Закрыть") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmation = false }) {
                    Text("Отменить")
                }
            })
    }
}
