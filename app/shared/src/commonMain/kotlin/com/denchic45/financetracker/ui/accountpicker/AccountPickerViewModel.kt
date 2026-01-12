package com.denchic45.financetracker.ui.accountpicker

import androidx.compose.runtime.mutableStateSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.usecase.ObserveAccountsUseCase
import com.denchic45.financetracker.ui.PickerMode
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.hasResult
import com.denchic45.financetracker.ui.resource.onData
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class AccountPickerViewModel(
    private val mode: PickerMode,
    private val accountPickerInteractor: AccountPickerInteractor,
    observeAccountsUseCase: ObserveAccountsUseCase,
    private val router: AppRouter
) : ViewModel() {
    val selectedAccounts = mutableStateSetOf<AccountItem>()

    val isMultipleMode: Boolean = mode is PickerMode.Multiple

    @OptIn(ExperimentalCoroutinesApi::class)
    val accounts = observeAccountsUseCase().stateInCacheableResource(viewModelScope)

    init {
        viewModelScope.launch {
            accounts.first { it.hasResult() }
                .onData { _, items ->
                    val initialIds = when (mode) {
                        is PickerMode.Single -> mode.initialId?.let { setOf(it) } ?: emptySet()
                        is PickerMode.Multiple -> mode.initialIds
                    }

                    selectedAccounts.addAll(items.filter { initialIds.contains(it.id) })
                }
        }
    }

    fun onAccountSelect(item: AccountItem) {
        when (mode) {
            is PickerMode.Single -> {
                viewModelScope.launch {
                    accountPickerInteractor.onSelect(item)
                    router.pop()
                }
            }

            is PickerMode.Multiple -> {
                if (item in selectedAccounts) {
                    selectedAccounts.remove(item)
                } else {
                    selectedAccounts.add(item)
                }
            }
        }
    }

    fun onDoneClick() {
        viewModelScope.launch {
            accountPickerInteractor.onSelectMultiple(selectedAccounts.toList())
        }
        router.pop()
    }

    fun onDismissClick() {
        router.pop()
        viewModelScope.launch { accountPickerInteractor.onDismiss() }
    }

    fun onAddAccountClick() = router.push(NavEntry.AccountEditor(null))
}