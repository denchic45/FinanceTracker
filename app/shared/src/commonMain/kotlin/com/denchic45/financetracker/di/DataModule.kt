package com.denchic45.financetracker.di

import com.denchic45.financetracker.data.AppPreferences
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.data.repository.CategoryRepository
import com.denchic45.financetracker.data.repository.SettingsRepository
import com.denchic45.financetracker.data.repository.TagRepository
import com.denchic45.financetracker.data.repository.TransactionRepository
import com.denchic45.financetracker.data.service.AuthService
import com.denchic45.financetracker.data.service.StatisticsService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect val platformDataModule: Module

val repositoryModule = module {
    singleOf(::AccountRepository)
    singleOf(::TransactionRepository)
    singleOf(::CategoryRepository)
    singleOf(::SettingsRepository)
    singleOf(::TagRepository)
    singleOf(::StatisticsService)
}


val daoModule = module {
    single { get<AppDatabase>().accountDao }
    single { get<AppDatabase>().categoryDao }
    single { get<AppDatabase>().transactionDao }
    single { get<AppDatabase>().tagDao }
}

val dataModule = module {
    singleOf(::AppPreferences)
    singleOf(::AuthService)
    includes(repositoryModule, platformDataModule, daoModule)
}