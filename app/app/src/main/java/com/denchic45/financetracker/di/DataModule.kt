package com.denchic45.financetracker.di

import android.content.Context
import androidx.room.Room
import com.denchic45.financetracker.data.AppPreferences
import com.denchic45.financetracker.data.dataStore
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.data.repository.CategoryRepository
import com.denchic45.financetracker.data.repository.TagRepository
import com.denchic45.financetracker.data.repository.TransactionRepository
import com.denchic45.financetracker.data.service.AuthService
import com.denchic45.financetracker.data.service.StatisticsService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AccountRepository)
    singleOf(::TransactionRepository)
    singleOf(::CategoryRepository)
    singleOf(::TagRepository)
    singleOf(::StatisticsService)
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "database"
        ).build()
    }
    single { get<AppDatabase>().accountDao }
    single { get<AppDatabase>().categoryDao }
    single { get<AppDatabase>().transactionDao }
    single { get<AppDatabase>().tagDao }
}

val dataModule = module {
    single { get<Context>().dataStore }
    singleOf(::AppPreferences)
    singleOf(::AuthService)
    includes(repositoryModule, databaseModule)
}