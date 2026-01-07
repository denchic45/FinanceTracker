package com.denchic45.financetracker.ui.transactioneditor

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.api.transaction.model.TransactionType
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TagItem
import com.denchic45.financetracker.domain.model.displayName
import com.denchic45.financetracker.ui.CurrencyVisualTransformation
import com.denchic45.financetracker.ui.PlainTextTextField
import com.denchic45.financetracker.ui.RemoveTransactionConfirmDialog
import com.denchic45.financetracker.ui.accounts.AccountTypeIcon
import com.denchic45.financetracker.ui.categorizedIcons
import com.denchic45.financetracker.ui.util.formattedDateTime
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.arrow_back
import financetracker_app.shared.generated.resources.calendar_event
import financetracker_app.shared.generated.resources.check
import financetracker_app.shared.generated.resources.clock
import financetracker_app.shared.generated.resources.common_back
import financetracker_app.shared.generated.resources.common_cancel
import financetracker_app.shared.generated.resources.common_ok
import financetracker_app.shared.generated.resources.common_save
import financetracker_app.shared.generated.resources.common_select
import financetracker_app.shared.generated.resources.common_time_input_dialog_title
import financetracker_app.shared.generated.resources.tags
import financetracker_app.shared.generated.resources.tags_title
import financetracker_app.shared.generated.resources.txn_category_required
import financetracker_app.shared.generated.resources.txn_income_account_hint
import financetracker_app.shared.generated.resources.txn_new
import financetracker_app.shared.generated.resources.txn_note_field_hint
import financetracker_app.shared.generated.resources.txn_source_account_hint
import financetracker_app.shared.generated.resources.txn_update
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TransactionEditorScreen(
    transactionId: Long?,
    viewModel: TransactionEditorViewModel = koinViewModel {
        parametersOf(transactionId)
    }
) {
    val state = viewModel.state
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showConfirmToDelete by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDate = state.datetime.date.toJavaLocalDate()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateChange(
                        Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    )
                    showDatePicker = false
                }) {
                    Text(stringResource(Res.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            is24Hour = true,
            initialHour = state.datetime.hour,
            initialMinute = state.datetime.minute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(Res.string.common_time_input_dialog_title)) },
            text = {
                TimeInput(timePickerState)
            },
            confirmButton = {
                Button(onClick = {
                    showTimePicker = false
                    viewModel.onTimeChange(LocalTime(timePickerState.hour, timePickerState.minute))
                }) { Text(stringResource(Res.string.common_select)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            }
        )
    }

    if (showConfirmToDelete) {
        RemoveTransactionConfirmDialog(
            onConfirm = {
                showConfirmToDelete = false
                viewModel.onRemoveClick()
            },
            onDismiss = {
                showConfirmToDelete = false
            }
        )
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(if (transactionId == null) Res.string.txn_new else Res.string.txn_update),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = viewModel::onDismissClick) {
                        Icon(
                            painterResource(Res.drawable.arrow_back),
                            contentDescription = stringResource(Res.string.common_back)
                        )
                    }
                },
                actions = {
                    if (state.isLoading)
                        IconButton(onClick = {}) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    else IconButton(onClick = viewModel::onSaveClick) {
                        Icon(
                            painterResource(Res.drawable.check),
                            contentDescription = stringResource(Res.string.common_save)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TransactionTypeSelector(
                selectedType = state.transactionType,
                onTypeSelected = viewModel::onTransactionTypeChange
            )

            PlainTextTextField(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                value = state.amountText,
                onValueChange = viewModel::onAmountTextChange,
                textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center),
                placeholderText = "0.00 ₽",
                isError = state.amountErrorType != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                visualTransformation = CurrencyVisualTransformation()
            )

            Row {
                ListItem(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showDatePicker = true
                        },
                    headlineContent = {
                        Text(
                            state.datetime.formattedDateTime,
                            modifier = Modifier
                        )
                    },
                    leadingContent = { Icon(painterResource(Res.drawable.calendar_event), null) },
                )
                ListItem(
                    modifier = Modifier
                        .width(124.dp)
                        .clickable {
                            showTimePicker = true
                        },
                    headlineContent = {
                        Text(
                            state.datetime.time.format(LocalTime.Format {
                                hour(); char(':'); minute()
                            }),
                            modifier = Modifier
                        )
                    },
                    leadingContent = { Icon(painterResource(Res.drawable.clock), null) },
                )
            }

            AccountSelector(
                label = stringResource(if (state.transactionType == TransactionType.TRANSFER) Res.string.txn_source_account_hint else Res.string.txn_income_account_hint),
                selectedItem = state.sourceAccount,
                onClick = viewModel::onSourceAccountPickerClick,
                isError = state.showSourceAccountError
            )

            if (state.transactionType == TransactionType.TRANSFER) {
                AccountSelector(
                    label = stringResource(Res.string.txn_income_account_hint),
                    selectedItem = state.incomeAccount,
                    onClick = viewModel::onIncomeAccountPickerClick,
                    isError = state.showIncomeAccountError
                )
            }

            if (state.transactionType != TransactionType.TRANSFER) {
                CategorySelector(
                    selectedItem = state.category,
                    onClick = viewModel::onCategoryPickerClick,
                    isError = state.showCategoryError,
                )
            }

            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text(stringResource(Res.string.txn_note_field_hint)) },
                supportingText = { },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            TagsSelector(
                selectedTags = state.tags,
                onClick = viewModel::onTagsPickerClick
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    val selectedIndex = TransactionType.entries.indexOf(selectedType)

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup()
    ) {
        TransactionType.entries.forEachIndexed { index, type ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onTypeSelected(type) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = TransactionType.entries.size
                ),
                modifier = Modifier.selectable(
                    selected = index == selectedIndex,
                    onClick = { onTypeSelected(type) },
                    role = Role.RadioButton
                )
            ) {
                Text(type.displayName())
            }
        }
    }
}

@Composable
private fun AccountSelector(
    label: String,
    selectedItem: AccountItem?,
    onClick: () -> Unit,
    isError: Boolean,
) {
    Card(
        onClick = onClick,
        colors = if (isError) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ) else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedItem?.let {
                AccountTypeIcon(selectedItem.type)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                selectedItem?.name ?: label,
                color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        }
    }
}

@Composable
private fun CategorySelector(
    selectedItem: CategoryItem?,
    onClick: () -> Unit,
    isError: Boolean
) {
    Card(
        onClick = onClick,
        colors = if (isError) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ) else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedItem?.let {
                Icon(painterResource(categorizedIcons.getValue(selectedItem.iconName)), null)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                selectedItem?.name ?: stringResource(Res.string.txn_category_required),
                color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        }
    }
}

@Composable
private fun TagsSelector(
    selectedTags: List<TagItem>,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.tags),
                contentDescription = stringResource(Res.string.tags_title),
            )
        },
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedTags.isEmpty()) Text(stringResource(Res.string.tags_title))

                selectedTags.forEach { tag ->
                    AssistChip(
                        onClick = onClick,
                        label = { Text(tag.name) }
                    )
                }
            }
        })
}
