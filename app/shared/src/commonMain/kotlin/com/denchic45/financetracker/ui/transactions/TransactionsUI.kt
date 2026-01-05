package com.denchic45.financetracker.ui.transactions

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.denchic45.financetracker.ui.RemoveTransactionConfirmDialog
import com.denchic45.financetracker.ui.TopAppBar
import com.denchic45.financetracker.ui.TransactionListItem
import com.denchic45.financetracker.ui.resource.PagingItemsContent
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.common_delete
import financetracker_app.shared.generated.resources.common_edit
import financetracker_app.shared.generated.resources.txn_list_empty
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


val IncomeColor = Color(0xFF4CAF50)
val ExpenseColor = Color(0xFFF44336)
val TransferColor = Color(0xFF2196F3)

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = koinViewModel()
) {
    val pagingItems = viewModel.transactions.collectAsLazyPagingItems()

//    val isRetrying by viewModel.isRetrying.collectAsState()
    var longPressedItem by remember { mutableIntStateOf(-1) }
    var confirmToRemoveItemId by remember { mutableLongStateOf(-1) }

    Scaffold(topBar = {
        TopAppBar(
            title = {},
            navigateBack = viewModel::onDismissClick
        )
    }) { padding ->
        PagingItemsContent(
            modifier = Modifier.padding(padding),
            pagingItems = pagingItems,
            dataContent = {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(
                        count = pagingItems.itemCount,
                        key = pagingItems.itemKey { it.id }) { index ->
                        pagingItems[index]?.let { item ->
                            TransactionListItem(
                                modifier = Modifier
                                    .animateItem()
                                    .combinedClickable(
                                        onLongClick = { longPressedItem = index },
                                        onClick = { viewModel.onShowTransactionDetailsClick(item.id) }
                                    ),
                                transaction = item,
                                onClick = { viewModel.onTransactionClick(item.id) }
                            )
                            DropdownMenu(
                                expanded = longPressedItem == index,
                                onDismissRequest = { longPressedItem == -1 }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.common_edit)) },
                                    onClick = {
                                        longPressedItem == -1
                                        viewModel.onEditTransactionClick(item.id)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.common_delete)) },
                                    onClick = {
                                        longPressedItem == -1
                                        confirmToRemoveItemId == item.id
                                    }
                                )
                            }
                        }
                    }
                }
            },
            emptyDataContent = { Text(stringResource(Res.string.txn_list_empty)) }
        )
    }

    if (confirmToRemoveItemId != -1L) {
        RemoveTransactionConfirmDialog(
            onConfirm = {
                viewModel.onRemoveTransactionClick(confirmToRemoveItemId)
                confirmToRemoveItemId = -1L
            },
            onDismiss = { confirmToRemoveItemId = -1L },
        )
    }
}