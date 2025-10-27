package com.denchic45.financetracker.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.denchic45.financetracker.ui.accounteditor.AccountEditorDialog
import com.denchic45.financetracker.ui.analytics.AnalyticsScreen
import com.denchic45.financetracker.ui.labels.CategoriesScreen
import com.denchic45.financetracker.ui.home.HomeScreen
import com.denchic45.financetracker.ui.transactiondetails.TransactionDetailsSheet
import com.denchic45.financetracker.ui.transactions.TransactionsScreen
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MainScreen() {
    val viewModel = koinViewModel<MainViewModel>()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val current = viewModel.backStack.last()
            NavigationBar {
                NavigationBarItem(
                    selected = current is Screen.Home,
                    onClick = viewModel::onHomeNavigate,
                    icon = {
                        Icon(Icons.Outlined.Home, null)
                    },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = current is Screen.Transactions,
                    onClick = viewModel::onTransactionsNavigate,
                    icon = {
                        Icon(Icons.Outlined.History, null)
                    },
                    label = { Text("Транзакции") }
                )
                NavigationBarItem(
                    selected = current is Screen.Analytics,
                    onClick = viewModel::onAnalyticsNavigate,
                    icon = {
                        Icon(Icons.Outlined.Analytics, null)
                    },
                    label = { Text("Аналитика") }
                )
                NavigationBarItem(
                    selected = current is Screen.Labels,
                    onClick = viewModel::onLabelsNavigate,
                    icon = {
                        Icon(Icons.AutoMirrored.Outlined.Label, null)
                    },
                    label = { Text("Ярлыки") }
                )
                NavigationBarItem(
                    selected = current is Screen.Settings,
                    onClick = viewModel::onSettingsNavigate,
                    icon = {
                        Icon(Icons.Outlined.Settings, null)
                    },
                    label = { Text("Настройки") }
                )
            }
        }) { innerPadding ->
        NavDisplay(
            backStack = viewModel.backStack, // Your custom-managed back stack
            modifier = Modifier.padding(innerPadding),
            transitionSpec = { // Define custom transitions for screen changes
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Screen.Home> {
                    HomeScreen()
                }
                entry<Screen.Transactions> {
                    TransactionsScreen(
                        navigateToTransactionEditor = {
                            viewModel.onTransactionEditorNavigate(it)
                        },
                        navigateToTransactionDetails = {
                            viewModel.onTransactionDetailsNavigate(it)
                        }
                    )
                }
                entry<Screen.Analytics> {
                    AnalyticsScreen()
                }
                entry<Screen.Labels> {
                    CategoriesScreen()
                }
                entry<Screen.Settings> {

                }

                entry<Screen.AccountEditor> { key ->
                    AccountEditorDialog(
                        accountId = key.accountId,
                        onFinish = { viewModel.onNavigateBack() }
                    )
                }
                entry<Screen.TransactionEditor> { key ->
                    TODO()
                }
                entry<Screen.TransactionDetails> { key ->
                    TransactionDetailsSheet(
                        transactionId = key.transactionId,
                        navigateBack = { viewModel.onNavigateBack() }
                    )
                }
            }
        )
    }
}