package com.denchic45.financetracker.domain.model

import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.ui.resource.UiText
import com.denchic45.financetracker.ui.resource.uiTextOf
import com.denchic45.financetracker.ui.util.convertToCurrency
import java.util.UUID

data class AccountItem(
    val id: UUID,
    val name: String,
    val type: AccountType,
    val balance: Long
) {
    val displayedBalance = balance.convertToCurrency()
}

val List<AccountItem>.displayedGeneralBalance: String
    get() = (sumOf(AccountItem::balance)).convertToCurrency()

val AccountType.displayName: UiText
    get() = when (this) {
        AccountType.ORDINARY -> uiTextOf("Обычный")
        AccountType.DEBT -> uiTextOf("Долговой")
        AccountType.SAVINGS -> uiTextOf("Сбережения")
    }