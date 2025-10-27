package com.denchic45.financetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.denchic45.financetracker.domain.model.TransactionItem
import org.koin.compose.viewmodel.koinViewModel

// --- Constants ---
val IncomeColor = Color(0xFF4CAF50)
val ExpenseColor = Color(0xFFF44336)
val TransferColor = Color(0xFF2196F3)

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
                    item,
                    Modifier
                        .animateItem()
                        .combinedClickable(
                            onLongClick = { longPressedItem = index },
                            onClick = { navigateToTransactionDetails(item.id) }
                        ),
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
fun TransactionListItem(transaction: TransactionItem, modifier: Modifier = Modifier) {
    val (amountColor, categoryLabel) = when (transaction) {
        is TransactionItem.Expense -> {
            // Access type-specific property `category` safely
            ExpenseColor to transaction.category.name
        }

        is TransactionItem.Income -> {
            // Access type-specific property `category` safely
            IncomeColor to transaction.category.name
        }

        is TransactionItem.Transfer -> {
            // Access type-specific property `incomeAccount` safely
            val destinationName = transaction.incomeAccount.name
            TransferColor to "Перевод: ${transaction.account.type.displayName} -> $destinationName"
        }
    }

    val amountPrefix = when (transaction) {
        is TransactionItem.Expense -> "-"
        is TransactionItem.Transfer -> ""
        else -> "+" // Income
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ListAlt,
            contentDescription = null,
            tint = amountColor,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(amountColor.copy(alpha = 0.1f))
                .padding(8.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Uses abstract property `note`
            Text(text = transaction.note, style = MaterialTheme.typography.titleMedium)

            // Display Category/Transfer details
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amountPrefix + transaction.formattedAmount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = amountColor
            )
            // Showing source account display name
            Text(
                text = "Счет: ${transaction.account.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}