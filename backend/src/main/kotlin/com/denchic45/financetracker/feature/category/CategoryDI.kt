package com.denchic45.financetracker.feature.category

import org.koin.dsl.module

val categoryModule = module {
    single { CategoryRepository() }
}