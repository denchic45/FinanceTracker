package com.denchic45.financetracker.api.statistic.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatisticsResponse(
    val totals: TotalsAmount? = null,
    val groupedAmounts: List<GroupedAmountPoint>? = null,
    val categoriesAmounts: CategorizedAmounts? = null,
    val tagsAmounts: List<TagAmount>? = null,
    val accounts: List<AccountStatisticsResponse>? = null
)

@Serializable
enum class StatisticsField {
    @SerialName("totals")
    TOTALS,

    @SerialName("grouped")
    GROUPED,

    @SerialName("categories")
    CATEGORIES,

    @SerialName("tags")
    TAGS,

    @SerialName("accounts")
    ACCOUNTS
}

@Serializable
enum class GroupingPeriod {
    @SerialName("day")
    DAY,
    @SerialName("week")
    WEEK,
    @SerialName("month")
    MONTH,
    @SerialName("year")
    YEAR
}