package com.denchic45.financetracker.di

import com.denchic45.financetracker.ui.categories.CategoriesViewModel
import com.denchic45.financetracker.ui.transactions.TransactionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::CategoriesViewModel)
}