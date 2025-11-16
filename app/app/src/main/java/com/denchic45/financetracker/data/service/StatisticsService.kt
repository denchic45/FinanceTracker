package com.denchic45.financetracker.data.service

import com.denchic45.financetracker.api.statistic.StatisticsApi
import com.denchic45.financetracker.api.statistic.model.StatisticsField
import com.denchic45.financetracker.data.safeFetch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class StatisticsService(
    private val statisticsApi: StatisticsApi
) {

    @OptIn(ExperimentalTime::class)
    suspend fun findTotalsByMonth() = safeFetch {
        val currentYearMonth =
            Clock.System.todayIn(TimeZone.currentSystemDefault()).yearMonth
        statisticsApi.getStatistics(
            fromDate = currentYearMonth.firstDay,
            toDate = currentYearMonth.lastDay,
            fields = listOf(StatisticsField.TOTALS)
        )
    }.map { it.totals!! }
}