package com.denchic45.financetracker.feature.statistics

import com.denchic45.financetracker.ktor.currentUserId
import com.denchic45.financetracker.ktor.getEnumOrFail
import com.denchic45.financetracker.ktor.getLocalDate
import com.denchic45.financetracker.ktor.getLocalDateOrFail
import com.denchic45.financetracker.api.statistic.model.GroupingPeriod
import com.denchic45.financetracker.api.statistic.model.StatisticsField
import com.denchic45.financetracker.util.respond
import com.denchic45.financetracker.util.toUUID
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.inject
import java.util.*


fun Application.configureStatistics() {
    routing {
        authenticate("auth-jwt") {
            route("/statistics") {
                val repository by inject<StatisticsService>()

                get {
                    val query = call.request.queryParameters

                    val fields = query["fields"]?.split(",")
                        ?.map { StatisticsField.valueOf(it.uppercase()) }
                        ?: listOf(StatisticsField.TOTALS)

                    val fromDate = query.getLocalDateOrFail("from_date")
                    val params = StatisticsQueryParameters(
                        fromDate = fromDate,
                        toDate = if (fields.contains(StatisticsField.GROUPED)) {
                            query.getLocalDateOrFail("to_date")
                        } else query.getLocalDate("to_date") ?: fromDate,
                        fields = fields,
                        groupBy = if (fields.contains(StatisticsField.GROUPED)) {
                            query.getEnumOrFail<GroupingPeriod>("group_by")
                        } else null,

                        filteredAccountIds = query["filteredAccountIds"]?.split(",")?.map { it.toUUID() },
                        filteredCategoryIds = query["filteredCategoryIds"]?.split(",")?.map { it.toLong() },
                        filteredTagIds = query["filteredTagIds"]?.split(",")?.map { it.toLong() }
                    )

                    repository.findStatistics(currentUserId(), params).respond()
                }
            }
        }
    }
}

data class StatisticsQueryParameters(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val fields: List<StatisticsField>,
    val groupBy: GroupingPeriod?,
    val filteredAccountIds: List<UUID>?,
    val filteredCategoryIds: List<Long>?,
    val filteredTagIds: List<Long>?
)