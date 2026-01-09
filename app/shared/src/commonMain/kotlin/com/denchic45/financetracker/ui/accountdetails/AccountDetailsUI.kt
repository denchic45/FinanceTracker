package com.denchic45.financetracker.ui.accountdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.displayName
import com.denchic45.financetracker.ui.accountIcons
import com.denchic45.financetracker.ui.accounts.displayedBalance
import com.denchic45.financetracker.ui.dialog.ConfirmDialog
import com.denchic45.financetracker.ui.resource.CacheableResourceContent
import com.denchic45.financetracker.ui.resource.CircularLoadingBox
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.account_delete_dialog_message
import financetracker_app.shared.generated.resources.account_delete_dialog_title
import financetracker_app.shared.generated.resources.common_delete
import financetracker_app.shared.generated.resources.common_edit
import financetracker_app.shared.generated.resources.edit
import financetracker_app.shared.generated.resources.trash
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsSheet(
    accountId: UUID,
    viewModel: AccountDetailsViewModel = koinViewModel {
        parametersOf(accountId)
    }
) {
    val accountResource by viewModel.account.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        RemoveAccountConfirmDialog(
            onConfirm = {
                showDeleteConfirmation = false
                viewModel.onRemoveClick()
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    ModalBottomSheet(onDismissRequest = viewModel::onDismissClick) {
        CacheableResourceContent(
            resource = accountResource,
            loadingContent = { CircularLoadingBox(Modifier.height(200.dp)) },
            dataContent = {
                AccountDetailsContent(
                    account = it,
                    onEditClick = viewModel::onEditClick,
                    onRemoveClick = { showDeleteConfirmation = true }
                )
            })
    }
}

@Composable
private fun AccountDetailsContent(
    account: AccountItem,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = {
                Icon(
                    painterResource(accountIcons.getValue(account.iconName)),
                    null,
                    modifier = Modifier.size(56.dp)
                )
            },
            headlineContent = {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            supportingContent = {
                Text(
                    text = account.type.displayName,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingContent = {
                Text(
                    text = account.displayedBalance,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        )


        HorizontalDivider(Modifier.padding(vertical = 24.dp))
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
                Icon(painterResource(Res.drawable.edit), "Edit")
                Text(
                    stringResource(Res.string.common_edit),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun RemoveAccountConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = stringResource(Res.string.account_delete_dialog_title),
        text = stringResource(Res.string.account_delete_dialog_message),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}
