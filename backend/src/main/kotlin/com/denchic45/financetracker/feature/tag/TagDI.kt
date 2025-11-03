package com.denchic45.financetracker.feature.tag

import org.koin.dsl.module

val tagModule = module {
    single { TagRepository() }
}