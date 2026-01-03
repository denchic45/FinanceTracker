package com.denchic45.financetracker.di

import com.denchic45.financetracker.domain.usecase.AddAccountUseCase
import com.denchic45.financetracker.domain.usecase.AddCategoryUseCase
import com.denchic45.financetracker.domain.usecase.AddTagUseCase
import com.denchic45.financetracker.domain.usecase.AddTransactionUseCase
import com.denchic45.financetracker.domain.usecase.FindTotalStatisticsUseCase
import com.denchic45.financetracker.domain.usecase.ObserveAccountByIdUseCase
import com.denchic45.financetracker.domain.usecase.ObserveAccountsUseCase
import com.denchic45.financetracker.domain.usecase.ObserveAuthStateUseCase
import com.denchic45.financetracker.domain.usecase.ObserveCategoriesUseCase
import com.denchic45.financetracker.domain.usecase.ObserveCategoryByIdUseCase
import com.denchic45.financetracker.domain.usecase.ObserveLatestTransactionsUseCase
import com.denchic45.financetracker.domain.usecase.ObserveTagByIdUseCase
import com.denchic45.financetracker.domain.usecase.ObserveTagsUseCase
import com.denchic45.financetracker.domain.usecase.ObserveTransactionByIdUseCase
import com.denchic45.financetracker.domain.usecase.ObserveTransactionsUseCase
import com.denchic45.financetracker.domain.usecase.RemoveAccountUseCase
import com.denchic45.financetracker.domain.usecase.RemoveCategoryUseCase
import com.denchic45.financetracker.domain.usecase.RemoveTagUseCase
import com.denchic45.financetracker.domain.usecase.RemoveTransactionUseCase
import com.denchic45.financetracker.domain.usecase.SignInUseCase
import com.denchic45.financetracker.domain.usecase.SignUpUseCase
import com.denchic45.financetracker.domain.usecase.UpdateAccountUseCase
import com.denchic45.financetracker.domain.usecase.UpdateCategoryUseCase
import com.denchic45.financetracker.domain.usecase.UpdateTagUseCase
import com.denchic45.financetracker.domain.usecase.UpdateTransactionUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val useCaseModule = module {
    // auth
    singleOf(::SignInUseCase)
    singleOf(::SignUpUseCase)
    singleOf(::ObserveAuthStateUseCase)

    // accounts
    singleOf(::ObserveAccountsUseCase)
    singleOf(::ObserveAccountByIdUseCase)
    singleOf(::AddAccountUseCase)
    singleOf(::UpdateAccountUseCase)
    singleOf(::RemoveAccountUseCase)

    // transactions
    singleOf(::AddTransactionUseCase)
    singleOf(::UpdateTransactionUseCase)
    singleOf(::ObserveLatestTransactionsUseCase)
    singleOf(::ObserveTransactionsUseCase)
    singleOf(::ObserveTransactionByIdUseCase)
    singleOf(::RemoveTransactionUseCase)

    // categories
    singleOf(::ObserveCategoriesUseCase)
    singleOf(::ObserveCategoryByIdUseCase)
    singleOf(::AddCategoryUseCase)
    singleOf(::UpdateCategoryUseCase)
    singleOf(::RemoveCategoryUseCase)

    // tags
    singleOf(::ObserveTagsUseCase)
    singleOf(::ObserveTagByIdUseCase)
    singleOf(::AddTagUseCase)
    singleOf(::UpdateTagUseCase)
    singleOf(::RemoveTagUseCase)

    // statistics
    singleOf(::FindTotalStatisticsUseCase)
}