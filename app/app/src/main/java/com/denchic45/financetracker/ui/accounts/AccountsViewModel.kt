package com.denchic45.financetracker.ui.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.ui.stateInCacheableResource
import com.denchic45.financetracker.domain.usecase.FindAccountsUseCase
import com.denchic45.financetracker.domain.usecase.RemoveAccountUseCase
import com.denchic45.financetracker.ui.RefreshDelegate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

class AccountsViewModel(
    private val findAccountsUseCase: FindAccountsUseCase,
    private val removeAccountUseCase: RemoveAccountUseCase,
) : ViewModel() {
    private val refreshDelegate = RefreshDelegate()
    private val retryDelegate = RefreshDelegate()

    @OptIn(ExperimentalCoroutinesApi::class)
    val accounts = merge(
        refreshDelegate.updateTrigger,
        retryDelegate.updateTrigger
    ).flatMapLatest {
        findAccountsUseCase()
    }.onEach {
        refreshDelegate.stopRefresh()
        retryDelegate.stopRefresh()
    }.stateInCacheableResource(viewModelScope)

    var showEditor by mutableStateOf<EditingAccountConfig?>(null)

    fun onAddClick() {
        showEditor = EditingAccountConfig(null)
    }

    fun onEditClick(accountId: UUID) {
        showEditor = EditingAccountConfig(accountId)
    }

    fun onRemoveClick(accountId: UUID) {
        viewModelScope.launch {
            removeAccountUseCase(accountId)
        }
    }

    fun onEditorFinish() {
        showEditor = null
    }
}

data class EditingAccountConfig(val accountId: UUID?)