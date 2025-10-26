package com.denchic45.financetracker.ui.accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.ui.CacheableResource
import com.denchic45.financetracker.ui.CircularLoadingBox
import com.denchic45.financetracker.ui.accounteditor.AccountEditorDialog
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(viewModel: AccountsViewModel = koinViewModel()) {
    val accountsResource by viewModel.accounts.collectAsState()
    var confirmToRemoveAccount by remember { mutableStateOf<UUID?>(null) }
    val showEditor = viewModel.showEditor

    if (confirmToRemoveAccount != null) {
        AlertDialog(
            onDismissRequest = { confirmToRemoveAccount = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onRemoveClick(confirmToRemoveAccount!!)
                    confirmToRemoveAccount = null
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirmToRemoveAccount = null
                }) { Text("Отмена") }
            },
            title = { Text("Удалить счет?") },
            text = { Text("Счет и все операции с ним будут удалены без возможности восстановления") }
        )
    }

    showEditor?.let { config ->
        AccountEditorDialog(accountId = config.accountId, onFinish = viewModel::onEditorFinish)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Счета") },
            actions = {
                IconButton(onClick = viewModel::onAddClick) {
                    Icon(Icons.Default.Add, null)
                }
            })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            when (val resource = accountsResource) {
                CacheableResource.Loading -> CircularLoadingBox()
                is CacheableResource.Newest -> {
                    AccountsList(
                        accounts = resource.value,
                        onEditClick = viewModel::onEditClick,
                        onRemoveClick = viewModel::onRemoveClick
                    )
                }

                is CacheableResource.Cached -> {
                    AccountsList(
                        accounts = resource.value,
                        onEditClick = viewModel::onEditClick,
                        onRemoveClick = viewModel::onRemoveClick
                    )
                }
                is CacheableResource.Failed -> {
                    TODO()
                }
            }

        }
    }
}

@Composable
private fun AccountsList(
    accounts: List<AccountResponse>,
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