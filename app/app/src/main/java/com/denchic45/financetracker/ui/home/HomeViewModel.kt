package com.denchic45.financetracker.ui.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.TransactionItem
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    val uiState = HomeUiState()

    private fun loadInitialData() {
        viewModelScope.launch {
            uiState.isLoading = true
            uiState.generalErrorMessage = null

            TODO()
        }
    }
}

@Stable
class HomeUiState {
    // --- Data Properties ---

    // General Balance (e.g., sum of all bills)
    var generalBalance by mutableStateOf(0.0)

    // Monthly Summary Stats
    var monthStats by mutableStateOf(MonthStat(expense = 0.0, income = 0.0, profit = 0.0))

    // Horizontal list of Bills/Accounts
    var accounts by mutableStateOf(emptyList<AccountItem>())

    // Vertical list of latest transactions
    var latestTransactions by mutableStateOf(emptyList<TransactionItem>())

    // --- UI/Async State ---

    // General loading indicator for initial data fetch or sync
    var isLoading by mutableStateOf(true)

    // Error message related to data fetch or synchronization
    var generalErrorMessage by mutableStateOf<String?>(null)
}

data class MonthStat(val expense: Double, val income: Double, val profit: Double)