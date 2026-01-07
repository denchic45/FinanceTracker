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
import financetracker_app.shared.generated.resources.common_delete
import financetracker_app.shared.generated.resources.common_duplicate
import financetracker_app.shared.generated.resources.common_edit
import financetracker_app.shared.generated.resources.edit
import financetracker_app.shared.generated.resources.note
import financetracker_app.shared.generated.resources.tags
import financetracker_app.shared.generated.resources.tags_title
import financetracker_app.shared.generated.resources.trash
import financetracker_app.shared.generated.resources.txn_category_label
import financetracker_app.shared.generated.resources.txn_credited_to_label
import financetracker_app.shared.generated.resources.txn_debited_from_label
import financetracker_app.shared.generated.resources.txn_note_field_hint
import financetracker_app.shared.generated.resources.txn_transfer
import financetracker_app.shared.generated.resources.txn_transfer_from_label
import financetracker_app.shared.generated.resources.txn_transfer_to_label
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
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
            Color(0xFF2E7D32),
            "+ ",
            transaction.category.name
        )

        is TransactionItem.Expense -> Triple(
            MaterialTheme.colorScheme.error,
            "- ",
            transaction.category.name
        )

        is TransactionItem.Transfer -> Triple(
            Color.Blue,
            "",
            stringResource(Res.string.txn_transfer)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (transaction) {
                is TransactionItem.Income -> {
                    DetailRow(
                        iconResource = Res.drawable.arrow_down,
                        label = stringResource(Res.string.txn_credited_to_label),
                        value = transaction.account.name
                    )
                    DetailRow(
                        iconResource = categorizedIcons.getValue(transaction.category.iconName),
                        label = stringResource(Res.string.txn_category_label),
                        value = transaction.category.name
                    )
                    if (transaction.tags.isNotEmpty()) TagsRow(transaction.tags)
                }

                is TransactionItem.Expense -> {
                    DetailRow(
                        iconResource = Res.drawable.arrow_up,
                        label = stringResource(Res.string.txn_debited_from_label),
                        value = transaction.account.name
                    )
                    DetailRow(
                        iconResource = categorizedIcons.getValue(transaction.category.iconName),
                        label = stringResource(Res.string.txn_category_label),
                        value = transaction.category.name
                    )
                    if (transaction.tags.isNotEmpty()) TagsRow(transaction.tags)
                }

                is TransactionItem.Transfer -> {
                    DetailRow(
                        iconResource = Res.drawable.arrow_up,
                        label = stringResource(Res.string.txn_transfer_from_label),
                        value = transaction.account.name
                    )
                    DetailRow(
                        iconResource = Res.drawable.arrow_down,
                        label = stringResource(Res.string.txn_transfer_to_label),
                        value = transaction.incomeAccount.name
                    )
                }
            }

            if (transaction.note.isNotBlank()) {
                EditableDetailRow(
                    iconResource = Res.drawable.note,
                    label = stringResource(Res.string.txn_note_field_hint),
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
                Text(
                    stringResource(Res.string.common_delete),
                    style = MaterialTheme.typography.labelSmall
                )
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
                Text(
                    stringResource(Res.string.common_edit),
                    style = MaterialTheme.typography.labelSmall
                )
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
                Text(
                    stringResource(Res.string.common_duplicate),
                    style = MaterialTheme.typography.labelSmall
                )
            }

        }
    }
}

@Composable
private fun TagsRow(tags: List<TagItem>) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = {
            Icon(
                painterResource(Res.drawable.tags),
                contentDescription = stringResource(Res.string.tags_title)
            )
        },
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
            painter = painterResource(iconResource),
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

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true, name = "Outcome Details")
@Composable
fun TransactionDetailsOutcomePreview() {
    // Моковые данные
    val mockAccount = AccountItem(UUID.randomUUID(), "Тинькофф Black", AccountType.ORDINARY, 500000)
    val mockCategory = CategoryItem(1, "Продукты", "food", false)
    val mockTransaction = TransactionItem.Expense(
        id = 1,
        amount = 12550, // 125.50
        datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        note = "Покупка в супермаркете 'Пятерочка'",
        account = mockAccount,
        category = mockCategory,
        tags = listOf(TagItem(1, "Еда"), TagItem(2, "Пятерочка"))
    )
    MaterialTheme {
        TransactionDetailsContent(
            transaction = mockTransaction,
            onEditClick = { },
            onRemoveClick = { }
        )
    }
}