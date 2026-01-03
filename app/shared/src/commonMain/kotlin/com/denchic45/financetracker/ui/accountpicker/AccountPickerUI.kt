package com.denchic45.financetracker.ui.accountpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.ui.resource.CacheableResourceListContent
import com.denchic45.financetracker.ui.resource.onData
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPickerSheet(
    viewModel: AccountPickerViewModel = koinViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()

    ModalBottomSheet(
        onDismissRequest = viewModel::onDismissClick,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column {
            accounts.onData { failure, items ->
                AccountList(
                    multiple = false,
                    accounts = items,
                    selectedItems = emptySet(),
                    onClick = viewModel::onAccountSelect
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPickerSheet(
    selectedIds: List<UUID>,
    viewModel: AccountPickerViewModel = koinViewModel {
        parametersOf(selectedIds)
    }
) {
    val accounts by viewModel.accounts.collectAsState()
    val selectedAccounts = viewModel.selectedAccounts

    ModalBottomSheet(
        onDismissRequest = viewModel::onDismissClick,
        sheetState = rememberModalBottomSheetState()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Select Accounts") })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = viewModel::onAddAccountClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Account")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                CacheableResourceListContent(
                    resource = accounts,
                    dataContent = {
                        AccountList(
                            multiple = true,
                            accounts = it,
                            selectedItems = selectedAccounts,
                            onClick = viewModel::onAccountSelect
                        )
                        Button(
                            onClick = viewModel::onDoneClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Confirm Selection")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AccountList(
    multiple: Boolean,
    accounts: List<AccountItem>,
    selectedItems: Set<AccountItem>,
    onClick: (AccountItem) -> Unit
) {
    LazyColumn {
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
        headlineContent = { Text(account.name) },
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = if (showCheckbox) {
            {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() }
                )
            }
        } else {
            null
        }
    )
}