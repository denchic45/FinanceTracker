package com.denchic45.financetracker.ui.categoryeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.ui.categorizedIcons
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.arrow_back
import financetracker_app.shared.generated.resources.category_new
import financetracker_app.shared.generated.resources.category_update
import financetracker_app.shared.generated.resources.check
import financetracker_app.shared.generated.resources.common_back
import financetracker_app.shared.generated.resources.common_name_field
import financetracker_app.shared.generated.resources.common_save
import financetracker_app.shared.generated.resources.icon_pick
import financetracker_app.shared.generated.resources.txn_expense
import financetracker_app.shared.generated.resources.txn_income
import financetracker_app.shared.generated.resources.validation_name_required
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen(
    categoryId: Long?,
    income: Boolean?,
    viewModel: CategoryEditorViewModel = koinViewModel {
        parametersOf(categoryId, income)
    }
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (categoryId == null) Res.string.category_new
                            else Res.string.category_update
                        )
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
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (categoryId == null) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        onClick = { viewModel.onIncomeChange(false) },
                        selected = !state.income
                    ) {
                        Text(stringResource(Res.string.txn_expense))
                    }
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        onClick = { viewModel.onIncomeChange(true) },
                        selected = state.income
                    ) {
                        Text(stringResource(Res.string.txn_income))
                    }
                }
            }
            val focusRequester = remember { FocusRequester() }
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                label = { Text(stringResource(Res.string.common_name_field)) },
                isError = state.showNameError,
                supportingText = { if (state.showNameError) Text(stringResource(Res.string.validation_name_required)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

@Composable
fun CategoryIconGridItem(
    iconResource: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
