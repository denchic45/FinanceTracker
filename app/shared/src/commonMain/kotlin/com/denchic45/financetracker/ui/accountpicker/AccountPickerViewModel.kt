package com.denchic45.financetracker.ui.accountpicker

import androidx.compose.runtime.mutableStateSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.usecase.ObserveAccountsUseCase
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.hasResult
import com.denchic45.financetracker.ui.resource.onData
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID


class AccountPickerViewModel(
    selectedAccountIds: Set<UUID>?,
    private val accountPickerInteractor: AccountPickerInteractor,
    observeAccountsUseCase: ObserveAccountsUseCase,
    private val router: AppRouter
) : ViewModel() {
    val selectedAccounts = mutableStateSetOf<AccountItem>()
    val multiple = selectedAccountIds != null

    @OptIn(ExperimentalCoroutinesApi::class)
    val accounts = observeAccountsUseCase().stateInCacheableResource(viewModelScope)

    init {
        viewModelScope.launch {
            accounts.first { it.hasResult() }.onData { _, items ->
                selectedAccounts.addAll(items.filter {
                    selectedAccountIds?.contains(it.id) == true
                })
            }
        }
    }

    fun onAccountSelect(item: AccountItem) {
        if (multiple) {
            if (item in selectedAccounts) selectedAccounts.remove(item)
            else selectedAccounts.add(item)
        } else {
            viewModelScope.launch { accountPickerInteractor.onSelect(item) }
            router.pop()
        }
    }

    fun onDoneClick() {
        viewModelScope.launch {
            accountPickerInteractor.onSelectMultiple(selectedAccounts.toList())
        }
        router.pop()
    }

    fun onDismissClick() = router.pop()

    fun onAddAccountClick() = router.push(NavEntry.AccountEditor(null))
}