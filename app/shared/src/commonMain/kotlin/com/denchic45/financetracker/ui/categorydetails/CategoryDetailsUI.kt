package com.denchic45.financetracker.ui.categorydetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.ui.RemoveCategoryConfirmDialog
import com.denchic45.financetracker.ui.categorizedIcons
import com.denchic45.financetracker.ui.resource.CircularLoadingBox
import com.denchic45.financetracker.ui.resource.onData
import com.denchic45.financetracker.ui.resource.onLoading
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.common_delete
import financetracker_app.shared.generated.resources.common_edit
import financetracker_app.shared.generated.resources.edit
import financetracker_app.shared.generated.resources.trash
import financetracker_app.shared.generated.resources.txn_expense
import financetracker_app.shared.generated.resources.txn_income
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsContent(
    categoryId: Long,
    viewModel: CategoryDetailsViewModel = koinViewModel {
        parametersOf(categoryId)
    }
) {
    val category by viewModel.category.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    if (showDeleteConfirmation) {
        RemoveCategoryConfirmDialog(
            onConfirm = {
                showDeleteConfirmation = false
                viewModel.onRemoveClick()
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    ModalBottomSheet(onDismissRequest = viewModel::onDismissClick) {
        category.onLoading { CircularLoadingBox(Modifier.height(160.dp)) }
            .onData { _, item ->
                CategoryDetailsContent(
                    category = item,
                    onEditClick = viewModel::onEditClick,
                    onRemoveClick = { showDeleteConfirmation = true }
                )
            }
    }
}

@Composable
private fun CategoryDetailsContent(
    category: CategoryItem,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(categorizedIcons.getValue(category.iconName)),
            contentDescription = category.name,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (category.income) stringResource(Res.string.txn_income) else stringResource(
                Res.string.txn_expense
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
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
                    contentDescription = stringResource(Res.string.common_delete),
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
                Icon(
                    painterResource(Res.drawable.edit),
                    stringResource(Res.string.common_edit)
                )
                Text(
                    stringResource(Res.string.common_edit),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}