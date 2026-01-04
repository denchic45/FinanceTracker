package com.denchic45.financetracker.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveTransactionsUseCase
import com.denchic45.financetracker.domain.usecase.RemoveTransactionUseCase
import com.denchic45.financetracker.ui.RefreshDelegate
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val removeTransactionUseCase: RemoveTransactionUseCase,
    private val router: AppRouter
) : ViewModel() {

    private val refresh = RefreshDelegate()
    private val retry = RefreshDelegate()
    val isRefreshing = refresh.isRefreshing
    val isRetrying = refresh.isRefreshing

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = observeTransactionsUseCase().cachedIn(viewModelScope)

    fun onRefresh() {
        viewModelScope.launch {
            refresh.refresh()
        }
    }

    fun onRetryClick() {
        viewModelScope.launch {
            retry.refresh()
        }
    }

    fun onRemoveTransactionClick(transactionId: Long) {
        viewModelScope.launch {
            removeTransactionUseCase(transactionId)
        }
    }

    fun onTransactionClick(transactionId: Long) {
        router.push(NavEntry.TransactionDetails(transactionId))
    }

    fun onShowTransactionDetailsClick(transactionId: Long) {
        router.push(NavEntry.TransactionDetails(transactionId))
    }

    fun onEditTransactionClick(transactionId: Long) {
        router.push(NavEntry.TransactionEditor(transactionId))
    }

    fun onDismissClick() = router.pop()
}