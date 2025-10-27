package com.denchic45.financetracker.di

import com.denchic45.financetracker.domain.usecase.FindAccountsUseCase
import com.denchic45.financetracker.domain.usecase.ObserveAuthStateUseCase
import com.denchic45.financetracker.domain.usecase.RemoveAccountUseCase
import com.denchic45.financetracker.domain.usecase.SignInUseCase
import com.denchic45.financetracker.domain.usecase.SignUpUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val useCaseModule = module {
    singleOf(::FindAccountsUseCase)
    singleOf(::ObserveAuthStateUseCase)
    singleOf(::RemoveAccountUseCase)
    singleOf(::SignInUseCase)
    singleOf(::SignUpUseCase)
}