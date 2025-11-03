package com.denchic45.financetracker.feature.statistics

import org.koin.dsl.module

val statisticsModule = module {
    single { StatisticsService() }
}