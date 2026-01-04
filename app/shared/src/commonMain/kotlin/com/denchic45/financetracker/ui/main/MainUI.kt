package com.denchic45.financetracker.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.denchic45.financetracker.ui.analytics.AnalyticsScreen
import com.denchic45.financetracker.ui.categories.CategoriesScreen
import com.denchic45.financetracker.ui.home.HomeScreen
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.analyze
import financetracker_app.shared.generated.resources.category
import financetracker_app.shared.generated.resources.home
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun toggleNavigationDrawer() {
        scope.launch {
            if (drawerState.isClosed) {
                drawerState.open()
            } else {
                drawerState.close()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Finance Tracker",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    NavigationDrawerItem(
                        label = { Text("Теги") },
                        selected = false,
                        onClick = {
                            toggleNavigationDrawer()
                            viewModel.onTagsClick()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    NavigationDrawerItem(
                        label = { Text("Данные") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Storage, contentDescription = null) },
                        onClick = {
                            toggleNavigationDrawer()
                            viewModel.onDataUsageClick()
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Настройки") },
                        selected = false,
                        icon = {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            toggleNavigationDrawer()
                            viewModel.onSettingsClick()
                        },
                    )
                    NavigationDrawerItem(
                        label = { Text("О приложении") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                        onClick = {
                            toggleNavigationDrawer()
                            viewModel.onAboutClick()
                        },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                val current = viewModel.navbarBackStack.last()
                NavigationBar {
                    NavigationBarItem(
                        selected = current is NavigationBarScreen.Home,
                        onClick = viewModel::onHomeNavigate,
                        icon = {
                            Icon(painterResource(Res.drawable.home), null)
                        },
                        label = { Text("Главная") }
                    )
                    NavigationBarItem(
                        selected = current is NavigationBarScreen.Analytics,
                        onClick = viewModel::onAnalyticsNavigate,
                        icon = {
                            Icon(painterResource(Res.drawable.analyze), null)
                        },
                        label = { Text("Аналитика") }
                    )
                    NavigationBarItem(
                        selected = current is NavigationBarScreen.Labels,
                        onClick = viewModel::onLabelsNavigate,
                        icon = {
                            Icon(painterResource(Res.drawable.category), null)
                        },
                        label = { Text("Категории") }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = viewModel::onCreateTransactionClick
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add transaction")
                }
            }
        ) { innerPadding ->
            NavDisplay(
                backStack = viewModel.navbarBackStack,
                modifier = Modifier.padding(innerPadding),
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<NavigationBarScreen.Home> {
                        HomeScreen(onNavigationDrawerClick = ::toggleNavigationDrawer)
                    }
                    entry<NavigationBarScreen.Analytics> {
                        AnalyticsScreen(onNavigationDrawerClick = ::toggleNavigationDrawer)
                    }
                    entry<NavigationBarScreen.Labels> {
                        CategoriesScreen(onNavigationDrawerClick = ::toggleNavigationDrawer)
                    }
                }
            )
        }
    }
}