package com.denchic45.financetracker.di

import org.koin.dsl.module

val appModule = module {
    includes(dataModule, viewModelModule)
}