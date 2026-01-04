package com.denchic45.financetracker.di

import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.main.NavEntry
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AppEventHandler)
    single { AppRouter(NavEntry.Main) }
}