package com.denchic45.financetracker.feature.account

import org.koin.dsl.module

val accountModule = module {
    single { AccountRepository() }
}