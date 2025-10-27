package com.denchic45.financetracker.ui.main

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import java.util.UUID


class MainViewModel() : ViewModel() {
    private val _backStack = mutableStateListOf<Screen>(Screen.Home)
    val backStack: List<Screen> = _backStack
    fun onTransactionEditorNavigate(transactionId: Long) {
        _backStack.add(Screen.TransactionEditor(transactionId))
    }

    fun onTransactionDetailsNavigate(transactionId: Long) {
        _backStack.add(Screen.TransactionDetails(transactionId))
    }

    fun onHomeNavigate() = _backStack.add(Screen.Home)

    fun onTransactionsNavigate() = _backStack.add(Screen.Transactions)

    fun onAnalyticsNavigate() = _backStack.add(Screen.Analytics)

    fun onLabelsNavigate() = _backStack.add(Screen.Labels)

    fun onSettingsNavigate() = _backStack.add(Screen.Settings)


    fun onNavigateBack() {
        _backStack.removeLastOrNull()
    }
}

@Serializable
sealed interface Screen {
    data object Home : Screen
    data object Analytics : Screen
    data object Transactions : Screen
    data object Labels : Screen

    data object Settings : Screen
    data class TransactionEditor(val transactionId: Long) : Screen
    data class TransactionDetails(val transactionId: Long) : Screen
    data class AccountEditor(val accountId: UUID) : Screen
}