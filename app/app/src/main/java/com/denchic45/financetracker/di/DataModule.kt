package com.denchic45.financetracker.di

import android.content.Context
import androidx.room.Room
import com.denchic45.financetracker.data.AppPreferences
import com.denchic45.financetracker.data.AuthService
import com.denchic45.financetracker.data.dataStore
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.repository.AccountRepository
import com.denchic45.financetracker.data.repository.TransactionRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AccountRepository)
    singleOf(::TransactionRepository)
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
}

val dataModule = module {
    single { get<Context>().dataStore }
    singleOf(::AppPreferences)
    singleOf(::AuthService)
    includes(repositoryModule, databaseModule)
}