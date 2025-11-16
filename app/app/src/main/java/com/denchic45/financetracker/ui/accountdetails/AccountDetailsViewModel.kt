package com.denchic45.financetracker.ui.accountdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import com.denchic45.financetracker.data.filterNotNullValue
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveAccountByIdUseCase
import com.denchic45.financetracker.domain.usecase.RemoveAccountUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.AppUIEvent
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import com.denchic45.financetracker.ui.resource.uiTextOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

class AccountDetailsViewModel(
    private val accountId: UUID,
    observeAccountByIdUseCase: ObserveAccountByIdUseCase,
    private val removeAccountUseCase: RemoveAccountUseCase,
    private val router: AppRouter,
    private val appEventHandler: AppEventHandler
) : ViewModel() {

    val account = observeAccountByIdUseCase(accountId)
        .onEach {
            it.getOrElse {
                router.pop()
                appEventHandler.sendEvent(AppUIEvent.AlertMessage(uiTextOf("Account not found")))
            }
        }
        .filterNotNullValue()
        .stateInCacheableResource(viewModelScope)

    fun onEditClick() {
        router.push(NavEntry.AccountEditor(accountId))
    }

    fun onRemoveClick() {
        viewModelScope.launch {
            removeAccountUseCase(accountId)
                .onSome { appEventHandler.handleFailure(it) }
                .onNone { router.pop() }
        }
    }

    fun onDismissClick() {
        router.pop()
    }
}
