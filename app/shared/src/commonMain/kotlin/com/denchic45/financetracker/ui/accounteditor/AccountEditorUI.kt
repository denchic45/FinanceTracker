package com.denchic45.financetracker.ui.accounteditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.domain.model.displayName
import com.denchic45.financetracker.ui.CurrencyVisualTransformation
import com.denchic45.financetracker.ui.LocalCurrencyHandler
import com.denchic45.financetracker.ui.LocalDefaultCurrency
import com.denchic45.financetracker.ui.accountIcons
import com.denchic45.financetracker.ui.accounts.AccountTypeIcon
import com.denchic45.financetracker.ui.dialog.ConfirmDiscardChangesDialog
import com.denchic45.financetracker.ui.iconpicker.IconGrid
import com.denchic45.financetracker.ui.iconpicker.IconGridItem
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.account_balance
import financetracker_app.shared.generated.resources.account_new
import financetracker_app.shared.generated.resources.account_type
import financetracker_app.shared.generated.resources.account_type_pick_title
import financetracker_app.shared.generated.resources.account_update
import financetracker_app.shared.generated.resources.arrow_back
import financetracker_app.shared.generated.resources.check
import financetracker_app.shared.generated.resources.common_back
import financetracker_app.shared.generated.resources.common_name_field
import financetracker_app.shared.generated.resources.icon_pick_title
import financetracker_app.shared.generated.resources.validation_balance_required
import financetracker_app.shared.generated.resources.validation_name_required
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditorScreen(
    accountId: UUID?, viewModel: AccountEditorViewModel = koinViewModel<AccountEditorViewModel> {
        parametersOf(accountId)
    }
) {
    val state = viewModel.state

    var showDiscardConfirmation by remember { mutableStateOf(false) }
    var accountTypesExpanded by remember { mutableStateOf(false) }
    var accountIconsExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    if (accountId != null) stringResource(Res.string.account_update) else stringResource(
                        Res.string.account_new
                    )
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    if (viewModel.hasChanges()) showDiscardConfirmation = true
                    else viewModel.onDismissClick()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = stringResource(Res.string.common_back)
                    )
                }
            }, actions = {
                if (state.isLoading) IconButton(onClick = {}) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), strokeWidth = 2.dp
                    )
                }
                else IconButton(onClick = viewModel::onSaveClick) {
                    Icon(
                        painterResource(Res.drawable.check), contentDescription = "Save changes"
                    )
                }
            })
        }) { paddingValues ->
        Column(Modifier.padding(paddingValues).padding(top = 16.dp)) {
            val focusRequester = remember { FocusRequester() }
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onAccountNameChanged,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                label = { Text(stringResource(Res.string.common_name_field)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
                isError = state.showNameError,
                supportingText = {
                    if (state.showNameError)
                        Text(stringResource(Res.string.validation_name_required))
                })

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            if (accountId == null) OutlinedTextField(
                value = state.balance,
                onValueChange = viewModel::onBalanceChanged,
                singleLine = true,
                label = { Text(stringResource(Res.string.account_balance)) },
                placeholder = { Text("10000 ₽") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                isError = state.showBalanceError,
                supportingText = {
                    if (state.showBalanceError)
                        Text(stringResource(Res.string.validation_balance_required))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                visualTransformation = CurrencyVisualTransformation(
                    LocalCurrencyHandler.current.decimalSeparator,
                    LocalDefaultCurrency.current
                )
            )

            ListItem(
                leadingContent = {
                    IconGridItem(
                        iconResource = accountIcons.getValue(state.iconName),
                        selected = true,
                        onClick = { accountIconsExpanded = true }
                    )
                },
                overlineContent = { Text(stringResource(Res.string.account_type)) },
                headlineContent = { Text(state.type.displayName) },
                modifier = Modifier.clickable(onClick = { accountTypesExpanded = true })
            )
        }
    }

    NavigationEventHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = viewModel.hasChanges()
    ) {
        showDiscardConfirmation = true
    }

    if (accountTypesExpanded) {
        AccountTypeSelectorSheet(
            selectedType = state.type,
            onSelect = {
                accountTypesExpanded = false
                viewModel.onAccountTypeChange(it)
            },
            onDismiss = { accountTypesExpanded = false }
        )
    }

    if (accountIconsExpanded) {
        AccountIconPickerSheet(
            selectedIconName = state.iconName,
            onPick = {
                viewModel.onIconPick(it)
                accountIconsExpanded = false
            },
            onDismiss = { accountIconsExpanded = false }
        )
    }

    ConfirmDiscardChangesDialog(
        show = showDiscardConfirmation,
        onConfirm = {
            showDiscardConfirmation = false
            viewModel.onDismissClick()
        }, onDismiss = {
            showDiscardConfirmation = false
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountIconPickerSheet(
    selectedIconName: String,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Text(
            stringResource(Res.string.icon_pick_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconGrid(
            selectedIconName = selectedIconName,
            onIconClick = onPick,
            icons = accountIcons.toList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTypeSelectorSheet(
    selectedType: AccountType,
    onSelect: (AccountType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Text(
            stringResource(Res.string.account_type_pick_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Column(Modifier.padding(vertical = 16.dp)) {
            AccountType.entries.forEach { type ->
                val selected = selectedType == type
                val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else LocalContentColor.current
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable(onClick = { onSelect(type) }),
                    headlineContent = {
                        Text(
                            type.displayName,
                            color = contentColor,
                            fontWeight = if (selected) FontWeight.SemiBold else null
                        )
                    },
                    leadingContent = { AccountTypeIcon(type = type, tint = contentColor) },
                    trailingContent = {
                        if (selected)
                            Icon(
                                painterResource(Res.drawable.check),
                                null,
                                tint = contentColor
                            )
                    }
                )
            }
        }
    }
}