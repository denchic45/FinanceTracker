package com.denchic45.financetracker.di

import com.denchic45.financetracker.domain.CurrencyHandler
import com.denchic45.financetracker.domain.JVMCurrencyHandler
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.main.NavEntry
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AppEventHandler)
    single<CurrencyHandler> { JVMCurrencyHandler() }
    single { AppRouter(NavEntry.Main) }
}