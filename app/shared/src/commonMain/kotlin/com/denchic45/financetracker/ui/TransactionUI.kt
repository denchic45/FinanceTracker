package com.denchic45.financetracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.ui.dialog.ConfirmDeletionDialog
import com.denchic45.financetracker.ui.transactions.ExpenseColor
import com.denchic45.financetracker.ui.transactions.IncomeColor
import com.denchic45.financetracker.ui.transactions.TransferColor
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.common_delete_dialog_message
import financetracker_app.shared.generated.resources.list
import financetracker_app.shared.generated.resources.txn_delete_dialog_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun TransactionListItem(
    modifier: Modifier = Modifier,
    transaction: TransactionItem,
    onClick: () -> Unit,
) {
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
            val senderName = transaction.account.name
            val destinationName = transaction.incomeAccount.name
            TransferColor to "$senderName -> $destinationName"
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
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(Res.drawable.list),
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
                text = amountPrefix + transaction.displayedAmount,
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

@Composable
fun RemoveTransactionConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDeletionDialog(
        title = stringResource(Res.string.txn_delete_dialog_title),
        text = stringResource(Res.string.common_delete_dialog_message),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}