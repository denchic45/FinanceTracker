package com.denchic45.financetracker.statistic

import com.denchic45.financetracker.response.ApiResult
import com.denchic45.financetracker.response.toResult
import com.denchic45.financetracker.statistic.model.GroupingPeriod // Assuming model imports
import com.denchic45.financetracker.statistic.model.StatisticsField   // Assuming model imports
import com.denchic45.financetracker.statistic.model.StatisticsResponse // Assuming this is the response type
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import java.util.UUID

class StatisticsApi(private val client: HttpClient) {

    /**
     * Retrieves financial statistics based on a set of query parameters.
     *
     * @param fromDate The required start date for the statistics (query parameter: "from_date").
     * @param toDate The optional end date for the statistics (query parameter: "to_date").
     * @param fields The list of statistic fields to include (query parameter: "fields"). Defaults to [StatisticsField.TOTALS].
     * @param groupBy The optional period to group data by (query parameter: "group_by"). Required if [StatisticsField.GROUPED] is in [fields].
     * @param filteredAccountIds Optional list of account IDs to filter by (query parameter: "filteredAccountIds").
     * @param filteredCategoryIds Optional list of category IDs to filter by (query parameter: "filteredCategoryIds").
     * @param filteredTagIds Optional list of tag IDs to filter by (query parameter: "filteredTagIds").
     */
    suspend fun getStatistics(
        fromDate: LocalDate,
        toDate: LocalDate? = null,
        fields: List<StatisticsField> = listOf(StatisticsField.TOTALS),
        groupBy: GroupingPeriod? = null,
        filteredAccountIds: List<UUID>? = null,
        filteredCategoryIds: List<Long>? = null,
        filteredTagIds: List<Long>? = null
    ): ApiResult<StatisticsResponse> {
        return client.get("/statistics") {
            parameter("from_date", fromDate.toString())
            toDate?.let { parameter("to_date", it.toString()) }

            parameter("fields", fields.joinToString(",") { it.name.uppercase() })

            groupBy?.let { parameter("group_by", it.name.uppercase()) }

            filteredAccountIds?.let { 
                parameter("filteredAccountIds", it.joinToString(",")) 
            }
            filteredCategoryIds?.let { 
                parameter("filteredCategoryIds", it.joinToString(",")) 
            }
            filteredTagIds?.let { 
                parameter("filteredTagIds", it.joinToString(",")) 
            }
        }.toResult()
    }
}