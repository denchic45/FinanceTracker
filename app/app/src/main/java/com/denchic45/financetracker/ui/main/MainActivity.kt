package com.denchic45.financetracker.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.denchic45.financetracker.ui.accounteditor.AccountEditorDialog
import com.denchic45.financetracker.ui.analytics.AnalyticsScreen
import com.denchic45.financetracker.ui.categories.LabelsScreen
import com.denchic45.financetracker.ui.home.HomeScreen
import com.denchic45.financetracker.ui.theme.FinanceTrackerTheme
import com.denchic45.financetracker.ui.transactiondetails.TransactionDetailsSheet
import com.denchic45.financetracker.ui.transactions.TransactionsScreen
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    val viewModel = koinViewModel<MainViewModel>()
    FinanceTrackerTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(Icons.Outlined.Home, null)
                        },
                        label = { Text("Главная") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(Icons.Outlined.Analytics, null)
                        },
                        label = { Text("Аналитика") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(Icons.Outlined.History, null)
                        },
                        label = { Text("Транзакции") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(Icons.AutoMirrored.Outlined.Label, null)
                        },
                        label = { Text("Ярлыки") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
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
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Screen.Home> {
                        HomeScreen()
                    }
                    entry<Screen.Analytics> {
                        AnalyticsScreen()
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
                    entry<Screen.Labels> {
                        LabelsScreen()
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
}