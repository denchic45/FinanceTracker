package com.denchic45.financetracker.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveAccountsUseCase
import com.denchic45.financetracker.domain.usecase.RemoveAccountUseCase
import com.denchic45.financetracker.ui.RefreshDelegate
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.refreshableFlow
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import kotlinx.coroutines.launch
import java.util.UUID

class AccountsViewModel(
    private val observeAccountsUseCase: ObserveAccountsUseCase,
    private val removeAccountUseCase: RemoveAccountUseCase,
    private val router: AppRouter
) : ViewModel() {
    private val refreshDelegate = RefreshDelegate()
    private val retryDelegate = RefreshDelegate()


    val accounts = refreshableFlow(refreshDelegate, retryDelegate) {
        observeAccountsUseCase()
    }.stateInCacheableResource(viewModelScope)


    fun onAddClick() {
        router.push(NavEntry.AccountEditor(null))
    }

    fun onEditClick(accountId: UUID) {
        router.push(NavEntry.AccountEditor(accountId))
    }

    fun onRemoveClick(accountId: UUID) {
        viewModelScope.launch {
            removeAccountUseCase(accountId)
        }
    }
}