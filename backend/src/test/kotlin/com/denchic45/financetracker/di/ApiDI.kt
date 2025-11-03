package com.denchic45.financetracker.di

import com.denchic45.financetracker.account.AccountApi
import com.denchic45.financetracker.auth.AuthApi
import com.denchic45.financetracker.category.CategoryApi
import com.denchic45.financetracker.statistic.StatisticsApi
import com.denchic45.financetracker.tag.TagApi
import com.denchic45.financetracker.transaction.TransactionApi
import org.koin.dsl.module

internal val apiModule = module {
    factory { AuthApi(it.get()) }
    factory { CategoryApi(it.get()) }
    factory { TagApi(get()) }
    factory { TransactionApi(it.get()) }
    factory { AccountApi(it.get()) }
    factory { StatisticsApi(it.get()) }
}