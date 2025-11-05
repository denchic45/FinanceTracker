package com.denchic45.financetracker.api.di

import com.denchic45.financetracker.api.account.AccountApi
import com.denchic45.financetracker.api.auth.AuthApi
import com.denchic45.financetracker.api.category.CategoryApi
import com.denchic45.financetracker.api.statistic.StatisticsApi
import com.denchic45.financetracker.api.tag.TagApi
import com.denchic45.financetracker.api.transaction.TransactionApi
import org.koin.dsl.module

internal val apiModule = module {
    factory { AuthApi(it.get()) }
    factory { CategoryApi(it.get()) }
    factory { TagApi(get()) }
    factory { TransactionApi(it.get()) }
    factory { AccountApi(it.get()) }
    factory { StatisticsApi(it.get()) }
}