package com.denchic45.financetracker.ui.transactions

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.denchic45.financetracker.domain.model.TransactionItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = koinViewModel(),
    navigateToTransactionEditor: (Long) -> Unit,
    navigateToTransactionDetails: (Long) -> Unit,
) {
    val items = viewModel.transactionsState.collectAsLazyPagingItems()
    var longPressedItem by remember { mutableIntStateOf(-1) }
    var confirmToRemoveItemId by remember { mutableLongStateOf(-1) }

    LazyColumn {
        items(
            count = items.itemCount,
            key = items.itemKey { it.id }) { index ->
            items[index]?.let { item ->
                TransactionListItem(
                    Modifier
                        .animateItem()
                        .combinedClickable(
                            onLongClick = { longPressedItem = index },
                            onClick = { navigateToTransactionDetails(item.id) }
                        ), item
                )
                DropdownMenu(
                    expanded = longPressedItem == index,
                    onDismissRequest = { longPressedItem == -1 }) {
                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        onClick = {
                            longPressedItem == -1
                            navigateToTransactionEditor(item.id)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = {
                            longPressedItem == -1
                            confirmToRemoveItemId == item.id
                        }
                    )
                }
            }
        }
    }

    if (confirmToRemoveItemId != -1L) {
        AlertDialog(
            onDismissRequest = { confirmToRemoveItemId = -1L },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onRemoveTransactionClick(confirmToRemoveItemId)
                    confirmToRemoveItemId = -1L
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirmToRemoveItemId = -1L
                }) { Text("Отмена") }
            },
            title = { Text("Удалить операцию?") },
            text = { Text("Операция будет удалена без возможности восстановления") }
        )
    }
}

@Composable
fun TransactionListItem(modifier: Modifier = Modifier, item: TransactionItem) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Column {
                Text(item.category.name)
                item.description.takeIf { it.isNotEmpty() }?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        trailingContent = {}
    )
}