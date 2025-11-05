package com.denchic45.financetracker.ui.transactiondetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TransactionItem
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsSheet(
    transactionId: Long,
    navigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<TransactionDetailsViewModel> {
        parametersOf(transactionId)
    }

//    AlertDialog(
//        onDismissRequest = { confirmToRemoveItemId = -1L },
//        confirmButton = {},
//        dismissButton = {},
//        title = { Text("Удалить операцию?") },
//        text = { Text("Операция будет удалена без возможности восстановления") }
//    )

    ModalBottomSheet(onDismissRequest = navigateBack) {
//        ResourceContent(
//            resource,
//            onLoading = { BottomSheetLoading() },
//            onFailed = { failure, value ->
//                value?.let {
//
//                } ?: ResultFailedBox(failure = failure)
//            }) { state ->
//
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsSheet(
    transaction: TransactionItem?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (transaction != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            TransactionDetailsContent(transaction = transaction)
        }
    }
}

@Composable
private fun TransactionDetailsContent(transaction: TransactionItem) {
    // 1. Determine display properties based on transaction type
    val (amountColor, amountPrefix, titleText) = when (transaction) {
        is TransactionItem.Income -> Triple(
            Color(0xFF2E7D32), // Custom Green (TODO fixed)
            "+ ",
            transaction.category.name
        )
        is TransactionItem.Expense -> Triple(
            MaterialTheme.colorScheme.error, // Changed to standard error color for better visibility
            "- ",
            transaction.category.name
        )
        is TransactionItem.Transfer -> Triple(
            Color.Blue,
            "",
            "Перевод"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Заголовок с суммой
        Text(
            text = "$amountPrefix${transaction.formattedAmount}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
        Text(
            text = titleText, // Use pre-calculated title
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        // Replaced deprecated HorizontalDivider
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Детали транзакции в зависимости от типа
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (transaction) { // FIX: Use 'when (transaction)' for type checking
                is TransactionItem.Income -> {
                    DetailRow(
                        icon = Icons.Default.ArrowDownward,
                        label = "Зачислено на счет",
                        value = transaction.account.name
                    )
                    DetailRow(
                        icon = getIconByName(transaction.category.icon),
                        label = "Категория",
                        value = transaction.category.name
                    )
                }

                is TransactionItem.Expense -> {
                    DetailRow(
                        icon = Icons.Default.ArrowUpward,
                        label = "Списано со счета",
                        value = transaction.account.name
                    )
                    DetailRow(
                        icon = getIconByName(transaction.category.icon),
                        label = "Категория",
                        value = transaction.category.name
                    )
                }

                is TransactionItem.Transfer -> {
                    DetailRow(
                        icon = Icons.Default.ArrowUpward,
                        label = "Со счета",
                        value = transaction.account.name
                    )
                    DetailRow(
                        icon = Icons.Default.ArrowDownward,
                        label = "На счет",
                        value = transaction.incomeAccount.name // Access type-specific field
                    )
                }
            }

            // 3. Описание (если есть) - Uses abstract property `note`
            if (transaction.note.isNotBlank()) {
                EditableDetailRow(
                    icon = Icons.AutoMirrored.Filled.Notes,
                    label = "Заметка",
                    value = transaction.note,
                    onValueChange = {}
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EditableDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = modifier) {
            // 1. The non-editable description/label
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 2. The editable TextField that looks like Text
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle.copy(
                    // Inherit color from the theme
                    color = MaterialTheme.colorScheme.onSurface
                ),
                // Ensure the cursor has a visible color
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true
            )
        }
    }
}

private fun getIconByName(name: String): ImageVector {
    // В реальном приложении здесь может быть маппинг строк на реальные ресурсы иконок
    return when (name) {
        "food" -> Icons.Default.Category // Пример
        else -> Icons.Default.Category
    }
}

@Preview(showBackground = true, name = "Outcome Details")
@Composable
fun TransactionDetailsOutcomePreview() {
    // Моковые данные
    val mockAccount = AccountItem(1, "Тинькофф Black", AccountType.CARD, 500000)
    val mockCategory = CategoryItem(1, "Продукты", "food", false)
    val mockTransaction = TransactionItem.Expense(
        id = 1,
        amount = 12550, // 125.50
        note = "Покупка в супермаркете 'Пятерочка'",
        account = mockAccount,
        category = mockCategory
    )
    MaterialTheme {
        TransactionDetailsContent(transaction = mockTransaction)
    }
}