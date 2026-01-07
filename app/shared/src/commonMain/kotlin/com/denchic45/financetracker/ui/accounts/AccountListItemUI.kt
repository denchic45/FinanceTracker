package com.denchic45.financetracker.ui.accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.domain.model.AccountItem
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.account_type
import financetracker_app.shared.generated.resources.common_delete
import financetracker_app.shared.generated.resources.common_edit
import financetracker_app.shared.generated.resources.credit_card
import financetracker_app.shared.generated.resources.more_vert
import financetracker_app.shared.generated.resources.note
import financetracker_app.shared.generated.resources.pig_money
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AccountListItem(account: AccountItem, onEditClick: () -> Unit, onRemoveClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Column {
                Text(account.name)
                Text(account.displayedBalance)
            }
        },
        leadingContent = {
            Box(
                Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AccountTypeIcon(account.type)
            }
        },
        trailingContent = {
            var showTrailingMenu by remember { mutableStateOf(false) }
            IconButton(onClick = { showTrailingMenu = true }) {
                Icon(painterResource(Res.drawable.more_vert), null)
            }

            DropdownMenu(
                expanded = showTrailingMenu,
                onDismissRequest = { showTrailingMenu = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.common_edit)) },
                    onClick = {
                        showTrailingMenu = false
                        onEditClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.common_delete)) },
                    onClick = {
                        showTrailingMenu = false
                        onRemoveClick()
                    }
                )
            }
        }
    )
}

@Composable
fun AccountTypeIcon(type: AccountType, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(
            when (type) {
                AccountType.ORDINARY -> Res.drawable.note
                AccountType.DEBT -> Res.drawable.credit_card
                AccountType.SAVINGS -> Res.drawable.pig_money
            }
        ),
        contentDescription = stringResource(Res.string.account_type),
        modifier = modifier
    )
}