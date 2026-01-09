package com.denchic45.financetracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.denchic45.financetracker.ui.auth.AuthScreen
import com.denchic45.financetracker.ui.main.MainNavigation
import com.denchic45.financetracker.ui.splash.Screen
import com.denchic45.financetracker.ui.splash.SplashViewModel
import com.denchic45.financetracker.ui.theme.FinanceTrackerTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceTrackerTheme(dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val viewModel = koinViewModel<SplashViewModel>()
                when (viewModel.screen) {
                    Screen.Auth -> AuthScreen()
                    Screen.Main -> MainNavigation()
                    Screen.Splash -> {}
                }
            }
        }
    }
}