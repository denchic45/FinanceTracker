package com.denchic45.financetracker.ui.main

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.navigation.router.Router
import com.denchic45.financetracker.ui.navigation.router.bringToFront
import com.denchic45.financetracker.ui.navigation.router.push
import kotlinx.serialization.Serializable
import java.util.UUID


class MainViewModel(
    appEventHandler: AppEventHandler,
    private val router: AppRouter
) : ViewModel() {
    private val navbarRouter: Router<NavigationBarScreen> = Router(NavigationBarScreen.Home)
    val navbarBackStack = navbarRouter.backstack

    val events = appEventHandler.events
    val loading = appEventHandler.loading

    fun onHomeNavigate() = navbarRouter.bringToFront(NavigationBarScreen.Home)

    fun onAnalyticsNavigate() = navbarRouter.bringToFront(NavigationBarScreen.Analytics)

    fun onLabelsNavigate() = navbarRouter.bringToFront(NavigationBarScreen.Labels)

    fun onCreateTransactionClick() {
        router.push(NavEntry.TransactionEditor(null))
    }

    fun onTagsClick() {
        router.push(NavEntry.Tags)
    }

    fun onDataUsageClick() {
        router.push(NavEntry.DataUsage)
    }

    fun onSettingsClick() {
        router.push(NavEntry.Settings)
    }

    fun onAboutClick() {
        router.push(NavEntry.About)
    }
}

@Serializable
sealed interface NavigationBarScreen : NavKey {
    data object Home : NavigationBarScreen, NavKey
    data object Analytics : NavigationBarScreen, NavKey
    data object Labels : NavigationBarScreen, NavKey
}

@Serializable
sealed interface NavEntry : NavKey {
    data object Main : NavEntry

    data object Tags : NavEntry
    data object DataUsage : NavEntry
    data object Settings : NavEntry
    data object About : NavEntry

    data class AccountDetails(val accountId: UUID) : NavEntry

    data object Transactions : NavEntry
    data class TransactionEditor(val transactionId: Long?) : NavEntry
    data class TransactionDetails(val transactionId: Long) : NavEntry

    data class CategoryDetails(val categoryId: Long) : NavEntry
    data class CategoryEditor(val categoryId: Long?, val income: Boolean?) : NavEntry
    data class CategoryPicker(val income: Boolean) : NavEntry

    data class AccountEditor(val accountId: UUID?) : NavEntry
    data class AccountPicker(val selectedIds: List<UUID>?) : NavEntry


    data class TagsPicker(val selectedIds: List<Long>) : NavEntry
    data class TagDetails(val tagId: Long) : NavEntry
    data class TagEditor(val tagId: Long?) : NavEntry
}