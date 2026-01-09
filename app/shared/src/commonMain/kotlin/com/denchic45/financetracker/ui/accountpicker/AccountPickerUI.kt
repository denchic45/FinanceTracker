package com.denchic45.financetracker.ui.accountpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.ui.PickerMode
import com.denchic45.financetracker.ui.accounts.AccountTypeIcon
import com.denchic45.financetracker.ui.accounts.displayedBalance
import com.denchic45.financetracker.ui.resource.CacheableResource
import com.denchic45.financetracker.ui.resource.CacheableResourceContent
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.account_pick_title
import financetracker_app.shared.generated.resources.check
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPickerSheet(
    mode: PickerMode,
    viewModel: AccountPickerViewModel = koinViewModel {
        parametersOf(mode)
    }
) {
    val accounts by viewModel.accounts.collectAsState()

    AccountPickerSheetContent(
        accounts = accounts,
        selectedItems = viewModel.selectedAccounts,
        isMultipleMode = viewModel.isMultipleMode,
        onPick = viewModel::onAccountSelect,
        onDoneClick = viewModel::onDoneClick,
        onDismissClick = viewModel::onDismissClick
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AccountPickerSheetContent(
    accounts: CacheableResource<List<AccountItem>>,
    selectedItems: Set<AccountItem>,
    isMultipleMode: Boolean,
    onPick: (AccountItem) -> Unit,
    onDoneClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissClick,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(Res.string.account_pick_title),
                style = MaterialTheme.typography.titleLarge
            )

            if (isMultipleMode) {
                androidx.compose.material3.TextButton(onClick = onDoneClick) {
                    Text("Готово") // Замените на stringResource, если нужно
                }
            }
        }

        CacheableResourceContent(
            resource = accounts,
            dataContent = { items ->
                AccountList(
                    multiple = isMultipleMode,
                    accounts = items,
                    selectedItems = selectedItems,
                    onClick = onPick
                )
            }
        )
    }
}

@Composable
fun AccountList(
    multiple: Boolean,
    accounts: List<AccountItem>,
    selectedItems: Set<AccountItem>,
    onClick: (AccountItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 16.dp)) {
        items(accounts) { account ->
            AccountListItem(
                account = account,
                showCheckbox = multiple,
                isSelected = account in selectedItems,
                onClick = { onClick(account) }
            )
        }
    }
}

@Composable
private fun AccountListItem(
    modifier: Modifier = Modifier,
    account: AccountItem,
    showCheckbox: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(account.name) },
        supportingContent = { Text(account.displayedBalance) },
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = { AccountTypeIcon(type = account.type) },
        trailingContent = when {
            showCheckbox -> {
                {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onClick() }
                    )
                }
            }

            isSelected -> {
                {
                    Icon(
                        painterResource(Res.drawable.check),
                        null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            else -> null
        }
    )
}