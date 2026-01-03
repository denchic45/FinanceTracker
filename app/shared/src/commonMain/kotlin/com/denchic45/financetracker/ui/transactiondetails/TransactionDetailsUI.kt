package com.denchic45.financetracker.ui.transactiondetails

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TagItem
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.ui.RemoveTransactionConfirmDialog
import com.denchic45.financetracker.ui.categorizedIcons
import com.denchic45.financetracker.ui.resource.CircularLoadingBox
import com.denchic45.financetracker.ui.resource.onData
import com.denchic45.financetracker.ui.resource.onLoading
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.arrow_down
import financetracker_app.shared.generated.resources.arrow_up
import financetracker_app.shared.generated.resources.clipboard_copy
import financetracker_app.shared.generated.resources.edit
import financetracker_app.shared.generated.resources.note
import financetracker_app.shared.generated.resources.tags
import financetracker_app.shared.generated.resources.trash
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsSheet(
    transactionId: Long,
    viewModel: TransactionDetailsViewModel = koinViewModel<TransactionDetailsViewModel> {
        parametersOf(transactionId)
    }
) {
    val transaction by viewModel.transaction.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    if (showDeleteConfirmation)
        RemoveTransactionConfirmDialog(
            onConfirm = {
                showDeleteConfirmation = false
                viewModel.onRemoveClick()
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    Log.d("TAG", "TransactionDetailsSheet: $transaction")

    ModalBottomSheet(
        onDismissRequest = viewModel::onDismissClick,
        sheetState = sheetState
    ) {
        transaction.onLoading { CircularLoadingBox(Modifier.height(160.dp)) }
            .onData { failure, item ->
                TransactionDetailsContent(
                    transaction = item,
                    onEditClick = viewModel::onEditClick,
                    onRemoveClick = { showDeleteConfirmation = true }
                )
            }
    }
}

@Composable
private fun TransactionDetailsContent(
    transaction: TransactionItem,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
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
            text = "$amountPrefix${transaction.displayedAmount}",
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
                        iconResource = Res.drawable.arrow_down,
                        label = "Зачислено на счет",
                        value = transaction.account.name
                    )
                    DetailRow(
                        iconResource = categorizedIcons.getValue(transaction.category.iconName),
                        label = "Категория",
                        value = transaction.category.name
                    )
                }

                is TransactionItem.Expense -> {
                    DetailRow(
                        iconResource = Res.drawable.arrow_up,
                        label = "Списано со счета",
                        value = transaction.account.name
                    )
                    DetailRow(
                        iconResource = categorizedIcons.getValue(transaction.category.iconName),
                        label = "Категория",
                        value = transaction.category.name
                    )
                }

                is TransactionItem.Transfer -> {
                    DetailRow(
                        iconResource = Res.drawable.arrow_up,
                        label = "Со счета",
                        value = transaction.account.name
                    )
                    DetailRow(
                        iconResource = Res.drawable.arrow_down,
                        label = "На счет",
                        value = transaction.incomeAccount.name
                    )
                }
            }

            // 3. Описание (если есть) - Uses abstract property `note`
            if (transaction.note.isNotBlank()) {
                EditableDetailRow(
                    iconResource = Res.drawable.note,
                    label = "Заметка",
                    value = transaction.note,
                    onValueChange = {}
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .clickable(onClick = onRemoveClick)
                    .height(56.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.trash),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text("Delete", style = MaterialTheme.typography.labelSmall)
            }

            Column(
                Modifier
                    .clickable(onClick = onEditClick)
                    .height(56.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(painterResource(Res.drawable.edit), null)
                Text("Edit", style = MaterialTheme.typography.labelSmall)
            }

            Column(
                Modifier
                    .clickable(onClick = { /*TODO*/ })
                    .height(56.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(painterResource(Res.drawable.clipboard_copy), null)
                Text("Duplicate", style = MaterialTheme.typography.labelSmall)
            }

        }
    }
}

@Composable
private fun TagsRow(tags: List<TagItem>) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = { Icon(painterResource(Res.drawable.tags), contentDescription = "Tags") },
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = { Text(tag.name) }
                    )
                }
            }
        }
    )
}

@Composable
private fun DetailRow(
    iconResource: DrawableResource,
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
    iconResource: DrawableResource,
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
            painter = painterResource(iconResource),
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

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true, name = "Outcome Details")
@Composable
fun TransactionDetailsOutcomePreview() {
    // Моковые данные
    val mockAccount = AccountItem(UUID.randomUUID(), "Тинькофф Black", AccountType.CARD, 500000)
    val mockCategory = CategoryItem(1, "Продукты", "food", false)
    val mockTransaction = TransactionItem.Expense(
        id = 1,
        amount = 12550, // 125.50
        datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        note = "Покупка в супермаркете 'Пятерочка'",
        account = mockAccount,
        category = mockCategory,
        tags = emptyList()
    )
    MaterialTheme {
        TransactionDetailsContent(
            transaction = mockTransaction,
            onEditClick = { },
            onRemoveClick = { }
        )
    }
}