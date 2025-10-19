package com.denchic45.financetracker.feature.auth

import org.koin.dsl.module

val authModule = module {
    single { AuthService() }
}