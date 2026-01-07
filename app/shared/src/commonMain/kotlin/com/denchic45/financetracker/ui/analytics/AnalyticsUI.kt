package com.denchic45.financetracker.ui.analytics

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.denchic45.financetracker.ui.MainTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = koinViewModel(),
    onNavigationDrawerClick: () -> Unit
) {
    Scaffold(
        topBar = {
            MainTopAppBar(
                onNavigationIconClick = onNavigationDrawerClick,
                actions = {}
            )
        }
    ) { padding ->

    }
}