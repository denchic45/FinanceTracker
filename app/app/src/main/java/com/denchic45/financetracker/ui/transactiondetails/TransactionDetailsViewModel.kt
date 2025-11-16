package com.denchic45.financetracker.ui.transactiondetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import com.denchic45.financetracker.data.filterNotNullValue
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveTransactionByIdUseCase
import com.denchic45.financetracker.domain.usecase.RemoveTransactionUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.AppUIEvent
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import com.denchic45.financetracker.ui.resource.uiTextOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
    private val transactionId: Long,
    observeTransactionByIdUseCase: ObserveTransactionByIdUseCase,
    private val removeTransactionUseCase: RemoveTransactionUseCase,
    private val router: AppRouter,
    private val appEventHandler: AppEventHandler
) : ViewModel() {

    val transaction = observeTransactionByIdUseCase(transactionId)
        .onEach {
            it.getOrElse {
                router.pop()
                appEventHandler.sendEvent(AppUIEvent.AlertMessage(uiTextOf("Transaction not found")))
            }

        }
        .filterNotNullValue()
        .stateInCacheableResource(viewModelScope)

    fun onEditClick() {
        router.push(NavEntry.TransactionEditor(transactionId))
    }

    fun onRemoveClick() {
        viewModelScope.launch {
            removeTransactionUseCase(transactionId)
                .onSome { appEventHandler.handleFailure(it) }
                .onNone { router.pop() }
        }
    }

    fun onDismissClick() {
        router.pop()
    }
}
