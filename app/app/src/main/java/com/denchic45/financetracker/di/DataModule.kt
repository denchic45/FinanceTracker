package com.denchic45.financetracker.di

import android.content.Context
import com.denchic45.financetracker.data.AppPreferences
import com.denchic45.financetracker.data.AuthService
import com.denchic45.financetracker.data.dataStore
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.data.repository.TransactionRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    single { get<Context>().dataStore }
    singleOf(::AppPreferences)
    singleOf(::AuthService)
    includes(repositoryModule)
}

val repositoryModule = module {
    singleOf(::AccountRepository)
    singleOf(::TransactionRepository)
}