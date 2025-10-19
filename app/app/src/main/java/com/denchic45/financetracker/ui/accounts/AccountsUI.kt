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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.ui.ResourceContent
import com.denchic45.financetracker.ui.accounteditor.AccountEditorDialog
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(viewModel: AccountsViewModel = koinViewModel()) {
    val accounts by viewModel.accounts.collectAsState()
    var confirmToRemoveAccount by remember { mutableLongStateOf(0L) }
    val showEditor = viewModel.showEditor

    if (confirmToRemoveAccount != 0L) {
        AlertDialog(
            onDismissRequest = { confirmToRemoveAccount = 0L },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onRemoveClick(confirmToRemoveAccount)
                    confirmToRemoveAccount = 0L
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirmToRemoveAccount = 0L
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
            ResourceContent(accounts) { accounts ->
                LazyColumn {
                    items(accounts) {
                        AccountListItem(
                            account = it,
                            onEditClick = { viewModel.onEditClick(it.id) },
                            onRemoveClick = { viewModel.onRemoveClick(it.id) }
                        )
                    }
                }
            }
        }
    }
}