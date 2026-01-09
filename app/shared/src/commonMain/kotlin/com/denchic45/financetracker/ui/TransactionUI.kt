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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.ui.accounts.AccountTypeIcon
import com.denchic45.financetracker.ui.dialog.ConfirmDeletionDialog
import com.denchic45.financetracker.ui.transactions.IncomeColor
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.arrow_forward
import financetracker_app.shared.generated.resources.common_delete_dialog_message
import financetracker_app.shared.generated.resources.transfer
import financetracker_app.shared.generated.resources.txn_delete_dialog_title
import financetracker_app.shared.generated.resources.txn_transfer
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.util.UUID


@Composable
fun TransactionListItem(
    modifier: Modifier = Modifier,
    transaction: TransactionItem,
    onClick: () -> Unit,
) {
    val amountColor = when (transaction) {
        is TransactionItem.Income -> IncomeColor
        is TransactionItem.Expense,
        is TransactionItem.Transfer -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val amountPrefix = when (transaction) {
        is TransactionItem.Income -> "+"
        is TransactionItem.Expense -> "-"
        is TransactionItem.Transfer -> ""
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(
                when (transaction) {
                    is TransactionItem.Expense -> categorizedIcons.getValue(transaction.category.iconName)
                    is TransactionItem.Income -> categorizedIcons.getValue(transaction.category.iconName)
                    is TransactionItem.Transfer -> Res.drawable.transfer
                }
            ),
            contentDescription = null,
            tint = amountColor,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (transaction is TransactionItem.Transfer) Color.Transparent
                    else amountColor.copy(alpha = 0.1f)
                )
                .padding(8.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when (transaction) {
                    is TransactionItem.Expense -> transaction.category.name
                    is TransactionItem.Income -> transaction.category.name
                    is TransactionItem.Transfer -> stringResource(Res.string.txn_transfer)
                }, style = MaterialTheme.typography.titleMedium
            )
            Row {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                    ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                        when (transaction) {
                            is TransactionItem.Expense,
                            is TransactionItem.Income -> {
                                AccountTypeIcon(
                                    type = transaction.account.type,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    transaction.account.name,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            is TransactionItem.Transfer -> {
                                AccountTypeIcon(
                                    type = transaction.account.type,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    transaction.account.name,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Icon(
                                    painterResource(Res.drawable.arrow_forward),
                                    null,
                                    modifier = Modifier.padding(horizontal = 8.dp).size(16.dp)
                                )
                                AccountTypeIcon(
                                    type = transaction.account.type,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    transaction.incomeAccount.name,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = amountPrefix + transaction.displayedAmount,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = amountColor
        )
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

@Preview
@Composable
private fun TransactionListItemPreview() {
    MaterialTheme {
        Column {
            TransactionListItem(
                transaction = TransactionItem.Expense(
                    id = 1,
                    amount = 5000,
                    datetime = LocalDateTime(2023, 10, 25, 12, 30),
                    note = "Groceries",
                    account = AccountItem(
                        id = UUID.randomUUID(),
                        name = "Main Card",
                        type = AccountType.ORDINARY,
                        balance = 100000,
                        iconName = "credit_card"
                    ),
                    category = CategoryItem(
                        id = 1,
                        name = "Food",
                        iconName = "burger",
                        income = false
                    ),
                    tags = emptyList()
                ),
                onClick = {}
            )
            TransactionListItem(
                transaction = TransactionItem.Income(
                    id = 2,
                    amount = 150000,
                    datetime = LocalDateTime(2023, 10, 25, 12, 30),
                    note = "Salary",
                    account = AccountItem(
                        id = UUID.randomUUID(),
                        name = "Main Card",
                        type = AccountType.ORDINARY,
                        balance = 250000,
                        iconName = "wallet"
                    ),
                    category = CategoryItem(
                        id = 2,
                        name = "Salary",
                        iconName = "pig_money",
                        income = true
                    ),
                    tags = emptyList()
                ),
                onClick = {}
            )
            TransactionListItem(
                transaction = TransactionItem.Transfer(
                    id = 3,
                    amount = 10000,
                    datetime = LocalDateTime(2023, 10, 25, 12, 30),
                    note = "To Savings",
                    account = AccountItem(
                        id = UUID.randomUUID(),
                        name = "Main Card",
                        type = AccountType.ORDINARY,
                        balance = 90000,
                        iconName = "credit_card"
                    ),
                    incomeAccount = AccountItem(
                        id = UUID.randomUUID(),
                        name = "Savings",
                        type = AccountType.SAVINGS,
                        balance = 50000,
                        iconName = "moneybag"
                    )
                ),
                onClick = {}
            )
        }
    }
}

@get:Composable
val TransactionItem.displayedAmount: String
    get() = LocalCurrencyHandler.current.formatForDisplay(amount, LocalDefaultCurrency.current)