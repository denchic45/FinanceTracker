package com.denchic45.financetracker.domain.usecase

import arrow.core.Either
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.service.StatisticsService
import com.denchic45.financetracker.api.statistic.model.TotalsAmount

class FindTotalStatisticsUseCase(private val statisticsService: StatisticsService) {
    suspend operator fun invoke(): Either<Failure, TotalsAmount> {
        return statisticsService.findTotalsByMonth()
    }
}