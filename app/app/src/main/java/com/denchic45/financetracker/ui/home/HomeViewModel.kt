package com.denchic45.financetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.FindTotalStatisticsUseCase
import com.denchic45.financetracker.domain.usecase.ObserveAccountsUseCase
import com.denchic45.financetracker.domain.usecase.ObserveLatestTransactionsUseCase
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import com.denchic45.financetracker.ui.resource.stateInResource
import kotlinx.coroutines.flow.map
import java.util.UUID

class HomeViewModel(
    private val findTotalStatisticsUseCase: FindTotalStatisticsUseCase,
    observeAccountsUseCase: ObserveAccountsUseCase,
    observeLatestTransactionsUseCase: ObserveLatestTransactionsUseCase,
    private val router: AppRouter
) : ViewModel() {

    val accounts = observeAccountsUseCase().stateInCacheableResource(viewModelScope)

    val monthStatistics = accounts.map { findTotalStatisticsUseCase() }
        .stateInResource(viewModelScope)

    val latestTransactions = observeLatestTransactionsUseCase()
        .stateInCacheableResource(viewModelScope)

    fun onAccountClick(accountId: UUID) = router.push(NavEntry.AccountDetails(accountId))

    fun onTransactionClick(transactionId: Long) {
        router.push(NavEntry.TransactionDetails(transactionId))
    }

    fun onShowMoreTransactionsClick() {
        router.push(NavEntry.Transactions)
    }

    fun onCreateAccountClick() {
        router.push(NavEntry.AccountEditor(null))
    }
}