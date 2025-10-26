package com.denchic45.financetracker.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.denchic45.financetracker.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val updateTrigger = Channel<Unit>()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionsState = updateTrigger.receiveAsFlow()
        .onStart { emit(Unit) }
        .flatMapLatest { transactionRepository.find() }
        .onEach { _isRefreshing.value = false }
        .cachedIn(viewModelScope)

    fun onRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            updateTrigger.send(Unit)
        }
    }

    fun onRemoveTransactionClick(transactionId: Long) {
        viewModelScope.launch {
            transactionRepository.remove(transactionId)
        }
    }
}