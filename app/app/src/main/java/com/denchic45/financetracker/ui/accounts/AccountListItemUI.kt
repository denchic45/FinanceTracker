package com.denchic45.financetracker.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Savings
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.account.model.AccountResponse
import com.denchic45.financetracker.account.model.AccountType

@Composable
fun AccountListItem(account: AccountResponse, onEditClick: () -> Unit, onRemoveClick: () -> Unit) {
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
                    .background(Color.Cyan)
            ) {
                AccountTypeIcon(account.type)
            }
        },
        trailingContent = {
            var showTrailingMenu by remember { mutableStateOf(false) }
            IconButton(onClick = { showTrailingMenu = true }) {
                Icon(Icons.Outlined.MoreVert, null)
            }

            DropdownMenu(
                expanded = showTrailingMenu,
                onDismissRequest = { showTrailingMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Изменить") },
                    onClick = {
                        showTrailingMenu = false
                        onEditClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Удалить") },
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
        imageVector = when (type) {
            AccountType.CASH -> Icons.AutoMirrored.Outlined.Note
            AccountType.CARD -> Icons.Outlined.CreditCard
            AccountType.BILL -> Icons.Outlined.Savings
        },
        contentDescription = "Account type",
        modifier = modifier
    )
}