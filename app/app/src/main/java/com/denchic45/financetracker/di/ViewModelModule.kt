package com.denchic45.financetracker.di

import com.denchic45.financetracker.ui.accounteditor.AccountEditorViewModel
import com.denchic45.financetracker.ui.accounts.AccountsViewModel
import com.denchic45.financetracker.ui.analytics.AnalyticsViewModel
import com.denchic45.financetracker.ui.auth.AuthViewModel
import com.denchic45.financetracker.ui.auth.SignInViewModel
import com.denchic45.financetracker.ui.auth.SignUpViewModel
import com.denchic45.financetracker.ui.labels.CategoriesViewModel
import com.denchic45.financetracker.ui.home.HomeViewModel
import com.denchic45.financetracker.ui.main.MainViewModel
import com.denchic45.financetracker.ui.root.RootViewModel
import com.denchic45.financetracker.ui.transactiondetails.TransactionDetailsViewModel
import com.denchic45.financetracker.ui.transactions.TransactionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::RootViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)

    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AccountsViewModel)
    viewModelOf(::AccountEditorViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::TransactionDetailsViewModel)
    viewModelOf(::CategoriesViewModel)
    viewModelOf(::AnalyticsViewModel)
}