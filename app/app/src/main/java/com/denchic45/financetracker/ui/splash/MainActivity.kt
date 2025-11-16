package com.denchic45.financetracker.ui.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.denchic45.financetracker.ui.auth.AuthScreen
import com.denchic45.financetracker.ui.main.RootContainer
import com.denchic45.financetracker.ui.theme.FinanceTrackerTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceTrackerTheme {
                val viewModel = koinViewModel<SplashViewModel>()
                when (viewModel.screen) {
                    Screen.Auth -> AuthScreen()
                    Screen.Main -> RootContainer()
                    Screen.Splash -> {}
                }
            }
        }
    }
}