package com.denchic45.financetracker.ui.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.denchic45.financetracker.ui.auth.AuthScreen
import com.denchic45.financetracker.ui.main.MainScreen
import com.denchic45.financetracker.ui.theme.FinanceTrackerTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootContainer()
        }
    }
}

@Composable
private fun RootContainer() {
    FinanceTrackerTheme {
        val viewModel = koinViewModel<RootViewModel>()
        NavDisplay(
            backStack = viewModel.backStack,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Screen.Main> {
                    MainScreen()
                }
                entry<Screen.Auth> {
                    AuthScreen()
                }
                entry<Screen.Splash> {

                }
            }
        )
    }
}