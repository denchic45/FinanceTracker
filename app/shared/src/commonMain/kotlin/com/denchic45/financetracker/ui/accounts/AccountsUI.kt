package com.denchic45.financetracker.ui.accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.ui.accountdetails.RemoveAccountConfirmDialog
import com.denchic45.financetracker.ui.resource.CacheableResourceListContent
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.accounts_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(viewModel: AccountsViewModel = koinViewModel()) {
    val accounts by viewModel.accounts.collectAsState()
    var confirmToRemoveAccount by remember { mutableStateOf<UUID?>(null) }

    if (confirmToRemoveAccount != null) {
        RemoveAccountConfirmDialog(
            onConfirm = {
                viewModel.onRemoveClick(confirmToRemoveAccount!!)
                confirmToRemoveAccount = null
            },
            onDismiss = {
                confirmToRemoveAccount = null
            }
        )
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.accounts_title)) },
            actions = {
                IconButton(onClick = viewModel::onAddClick) {
                    Icon(Icons.Default.Add, null)
                }
            })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            CacheableResourceListContent(
                resource = accounts,
                dataContent = {
                    AccountsList(
                        accounts = it,
                        onEditClick = viewModel::onEditClick,
                        onRemoveClick = viewModel::onRemoveClick
                    )
                }
            )
        }
    }
}

@Composable
private fun AccountsList(
    accounts: List<AccountItem>,
    onEditClick: (UUID) -> Unit,
    onRemoveClick: (UUID) -> Unit,
) {
    LazyColumn {
        items(accounts) {
            AccountListItem(
                account = it,
                onEditClick = { onEditClick(it.id) },
                onRemoveClick = { onRemoveClick(it.id) }
            )
        }
    }
}