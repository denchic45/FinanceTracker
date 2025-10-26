package com.denchic45.financetracker.ui.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.domain.stateInResource
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val accounts = accountRepository.findAll().stateInResource(viewModelScope)
    var showEditor by mutableStateOf<EditingAccountConfig?>(null)

    fun onAddClick() {
        showEditor = EditingAccountConfig(null)
    }

    fun onEditClick(accountId: UUID) {
        showEditor = EditingAccountConfig(accountId)
    }

    fun onRemoveClick(accountId: UUID) {
        viewModelScope.launch {
            accountRepository.remove(accountId)
        }
    }

    fun onEditorFinish() {
        showEditor = null
    }
}

data class EditingAccountConfig(val accountId: Long?)