package com.denchic45.financetracker.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.todayIn
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
fun LocalDateTime.formattedDateTime(showExtra: Boolean = false): String {
    val timeZone = TimeZone.currentSystemDefault()
    val daysDiff = day - Clock.System.todayIn(timeZone).day
    val pattern = when {
        daysDiff < 1 -> "dd MMMM"
        daysDiff < 14 -> "EEE, dd MMMM"
        else -> "EEE, dd MMMM uuuu"
    } + ", HH:mm"

    val extraText = when (daysDiff) {
        1 -> "Завтра"
        -1 -> "Вчера"
        else -> null
    }

    val dateTime = toJavaLocalDateTime().format(
        DateTimeFormatter.ofPattern(
            pattern,
            Locale.getDefault()
        )
    )
    return (extraText.takeIf { showExtra && it != null }
        ?.let { "$it ● " } ?: "") + dateTime
}

@OptIn(ExperimentalTime::class)
val LocalDate.formattedDate: String
    get() {
        val timeZone = TimeZone.currentSystemDefault()
        val daysDiff = day - Clock.System.todayIn(timeZone).day
        val pattern = when {
            daysDiff < 1 -> "dd MMMM"
            daysDiff < 14 -> "EEE, dd MMMM"
            else -> "EEE, dd MMMM uuuu"
        }

        return toJavaLocalDate().format(
            DateTimeFormatter.ofPattern(
                pattern,
                Locale.getDefault()
            )
        )
    }