package com.denchic45.financetracker.ui.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


/**
 * Russian month names from 'Январь' to 'Декабрь'.
 */
val RUSSIAN_FULL: MonthNames = MonthNames(
    listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
)

/**
 * Shortened Russian month names from 'Янв' to 'Дек'.
 */
val RUSSIAN_ABBREVIATED: MonthNames = MonthNames(
    listOf(
        "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
        "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
    )
)

@OptIn(ExperimentalTime::class)
val LocalDateTime.formattedDateTime: String
    get() {
        val timeZone = TimeZone.currentSystemDefault()
        val other = this.toInstant(timeZone)
        val now = Clock.System.now()
        val period = now.periodUntil(other, timeZone)
        val pattern = when {
            period.days < 1 -> "dd MMMM"
            period.days < 14 -> "EEE, dd MMMM"
            else -> "EEE, dd MMMM uuuu"
        }

        val daysDiff = now.daysUntil(other, timeZone)
        val extraText = when (daysDiff) {
            1 -> "Завтра"
            -1 -> "Вчера"
            else -> null
        }

        return toJavaLocalDateTime().format(
            DateTimeFormatter.ofPattern(
                pattern,
                Locale.getDefault()
            )
        ) + (extraText?.let { " ● $it" } ?: "")
    }