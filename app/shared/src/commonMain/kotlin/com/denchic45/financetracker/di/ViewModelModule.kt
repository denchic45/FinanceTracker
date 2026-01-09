package com.denchic45.financetracker.di

import com.denchic45.financetracker.ui.accountdetails.AccountDetailsViewModel
import com.denchic45.financetracker.ui.accounteditor.AccountEditorViewModel
import com.denchic45.financetracker.ui.accountpicker.AccountPickerViewModel
import com.denchic45.financetracker.ui.accounts.AccountsViewModel
import com.denchic45.financetracker.ui.analytics.AnalyticsViewModel
import com.denchic45.financetracker.ui.auth.AuthViewModel
import com.denchic45.financetracker.ui.auth.SignInViewModel
import com.denchic45.financetracker.ui.auth.SignUpViewModel
import com.denchic45.financetracker.ui.categories.CategoriesViewModel
import com.denchic45.financetracker.ui.categorydetails.CategoryDetailsViewModel
import com.denchic45.financetracker.ui.categoryeditor.CategoryEditorViewModel
import com.denchic45.financetracker.ui.categorypicker.CategoryPickerViewModel
import com.denchic45.financetracker.ui.home.HomeViewModel
import com.denchic45.financetracker.ui.main.MainViewModel
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.Router
import com.denchic45.financetracker.ui.settings.SettingsViewModel
import com.denchic45.financetracker.ui.splash.SplashViewModel
import com.denchic45.financetracker.ui.tagdetails.TagDetailsViewModel
import com.denchic45.financetracker.ui.tageditor.TagEditorViewModel
import com.denchic45.financetracker.ui.tags.TagsViewModel
import com.denchic45.financetracker.ui.tagspicker.TagsPickerViewModel
import com.denchic45.financetracker.ui.transactiondetails.TransactionDetailsViewModel
import com.denchic45.financetracker.ui.transactioneditor.TransactionEditorViewModel
import com.denchic45.financetracker.ui.transactions.TransactionsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

typealias AppRouter = Router<NavEntry>

val viewModelModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)

    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)

    viewModelOf(::AccountsViewModel)
    viewModel {
        AccountEditorViewModel(
            it.getOrNull(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { AccountPickerViewModel(it.get(), get(), get(), get()) }

    viewModelOf(::TransactionsViewModel)
    viewModel { TransactionDetailsViewModel(it.get(), get(), get(), get(), get()) }
    viewModel {
        TransactionEditorViewModel(
            it.getOrNull(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModelOf(::CategoriesViewModel)
    viewModel { CategoryPickerViewModel(it.get(), get(), get(), get()) }
    viewModel { TagDetailsViewModel(it.get(), get(), get(), get(), get()) }
    viewModelOf(::TagsViewModel)
    viewModel { TagsPickerViewModel(it.get(), get(), get(), get()) }

    viewModel {
        CategoryEditorViewModel(
            it.getOrNull(),
            it.getOrNull(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { CategoryDetailsViewModel(it.get(), get(), get(), get(), get()) }
    viewModel { TagEditorViewModel(it.getOrNull(), get(), get(), get(), get(), get()) }
    viewModel { AccountDetailsViewModel(it.get(), get(), get(), get(), get()) }

    viewModelOf(::AnalyticsViewModel)
}