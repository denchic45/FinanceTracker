package com.denchic45.financetracker.domain.model

import androidx.compose.runtime.Composable
import com.denchic45.financetracker.api.account.model.AccountType
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.account_type_debt
import financetracker_app.shared.generated.resources.account_type_ordinary
import financetracker_app.shared.generated.resources.account_type_savings
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

data class AccountItem(
    val id: UUID,
    val name: String,
    val type: AccountType,
    val balance: Long,
    val iconName: String
)

@Composable
fun AccountType.displayName(): String = stringResource(
    when (this) {
        AccountType.ORDINARY -> Res.string.account_type_ordinary
        AccountType.DEBT -> Res.string.account_type_debt
        AccountType.SAVINGS -> Res.string.account_type_savings
    }
)