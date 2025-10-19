package com.denchic45.financetracker.feature.transaction

import org.koin.dsl.module

val transactionModule = module {
    single { TransactionRepository() }
}