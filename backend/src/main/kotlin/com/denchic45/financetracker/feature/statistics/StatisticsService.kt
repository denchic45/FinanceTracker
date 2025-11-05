package com.denchic45.financetracker.feature.statistics

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.denchic45.financetracker.api.account.model.AccountResponse
import com.denchic45.financetracker.database.function.toChar
import com.denchic45.financetracker.database.table.*
import com.denchic45.financetracker.api.error.AccountNotFound
import com.denchic45.financetracker.api.error.ApiError
import com.denchic45.financetracker.api.error.CategoryNotFound
import com.denchic45.financetracker.api.error.TagNotFound
import com.denchic45.financetracker.feature.category.toCategoryResponse
import com.denchic45.financetracker.feature.tag.toTagResponse
import com.denchic45.financetracker.api.statistic.model.*
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter
import java.util.*


class StatisticsService {

    private fun Query.applyFilters(params: StatisticsQueryParameters): Query = apply {
        andWhere {
            (Transactions.datetime.date() greaterEq params.fromDate) and (Transactions.datetime.date() lessEq params.toDate)
        }

        params.filteredAccountIds?.takeIf { it.isNotEmpty() }?.let { ids ->
            andWhere { Transactions.sourceAccountId inList ids or (Transactions.incomeAccountId inList ids) }
        }

        params.filteredCategoryIds?.takeIf { it.isNotEmpty() }?.let { ids ->
            andWhere { Transactions.categoryId inList ids }
        }

        params.filteredTagIds?.takeIf { it.isNotEmpty() }?.let { ids ->
            adjustColumnSet {
                innerJoin(TransactionTags, { Transactions.id }, { TransactionTags.transactionId })
            }.andWhere { TransactionTags.tagId inList ids }
        }
    }

    fun findStatistics(ownerId: UUID, params: StatisticsQueryParameters): Either<ApiError, StatisticsResponse> =
        either {
            transaction {
                val baseQuery = {
                    Transactions.innerJoin(Accounts, { Transactions.sourceAccountId }, { Accounts.id }).selectAll()
                        .where(Accounts.ownerId eq ownerId).applyFilters(params)
                }

                val totals = if (StatisticsField.TOTALS in params.fields) findTotals(baseQuery())
                else null

                val groupedAmounts =
                    if (StatisticsField.GROUPED in params.fields && params.groupBy != null) findGroupedAmounts(
                        query = baseQuery(),
                        fromDate = params.fromDate,
                        toDate = params.toDate,
                        groupBy = params.groupBy
                    ) else null

                val categories = if (StatisticsField.CATEGORIES in params.fields) findCategorizedAmounts(baseQuery())
                else null


                val tagsAmounts = if (StatisticsField.TAGS in params.fields) findTagsAmounts(baseQuery())
                else null

                val accounts = if (StatisticsField.ACCOUNTS in params.fields) findAccountAmounts(baseQuery(), ownerId)
                else null

                StatisticsResponse(
                    totals = totals,
                    groupedAmounts = groupedAmounts,
                    categoriesAmounts = categories,
                    tagsAmounts = tagsAmounts,
                    accounts = accounts
                )
            }
        }


    fun findTotals(query: Query) =
        query.adjustSelect { select(Transactions.expenseSum, Transactions.incomeSum) }.firstOrNull()?.let {
            val expenses = it[Transactions.expenseSum] ?: 0L
            val incomes = it[Transactions.incomeSum] ?: 0L
            TotalsAmount(
                expenses = expenses, incomes = incomes, profit = incomes - expenses
            )
        }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private fun findGroupedAmounts(
        query: Query, fromDate: LocalDate, toDate: LocalDate, groupBy: GroupingPeriod
    ): List<GroupedAmountPoint> {
//        val actualGrouping = when (groupBy) {
//            // If query is for 21 days or less, use DAY, otherwise respect user choice
//            GroupingPeriod.DAY -> if (dateDiff <= 21) GroupingPeriod.DAY else groupBy
//            // If date range covers 2 full months or more, use MONTH, otherwise respect user choice
//            GroupingPeriod.WEEK -> if (dateDiff >= 60) GroupingPeriod.MONTH else GroupingPeriod.WEEK
//            else -> groupBy // MONTH or other explicit choice
//        }

        val groupingDateFormat = when (groupBy) {
            GroupingPeriod.DAY -> "yyyy-MM-dd"
            GroupingPeriod.WEEK -> "yyyy-ww"
            GroupingPeriod.MONTH -> "yyyy-MM"
            GroupingPeriod.YEAR -> "yyyy"
        }
        val dateGroupExpr = Transactions.datetime.toChar(groupingDateFormat)

        val result = query.adjustSelect { select(dateGroupExpr, Transactions.expenseSum, Transactions.incomeSum) }
            .groupBy(dateGroupExpr).orderBy(dateGroupExpr, SortOrder.ASC).associateBy { it[dateGroupExpr] }


        val range = when (groupBy) {
            GroupingPeriod.DAY -> (fromDate..toDate).step(1, DateTimeUnit.DAY).map {
                it.format(LocalDate.Format { byUnicodePattern(groupingDateFormat) })
            }

            GroupingPeriod.WEEK -> (fromDate..toDate).step(1, DateTimeUnit.WEEK).map {
                it.toJavaLocalDate().format(DateTimeFormatter.ofPattern(groupingDateFormat))
            }

            GroupingPeriod.MONTH -> (YearMonth(fromDate.year, fromDate.month)..YearMonth(
                toDate.year,
                toDate.month
            )).step(1, DateTimeUnit.MONTH).map {
                it.format(YearMonth.Format { byUnicodePattern(groupingDateFormat) })
            }

            GroupingPeriod.YEAR -> (fromDate.year..toDate.year).map { it.toString() }

        }

        return range.map { periodLabel ->
            result[periodLabel]?.let { row ->
                GroupedAmountPoint(
                    periodLabel = periodLabel,
                    totalExpenses = row[Transactions.expenseSum] ?: 0L,
                    totalIncomes = row[Transactions.incomeSum] ?: 0L
                )
            } ?: GroupedAmountPoint(
                periodLabel = periodLabel, totalExpenses = 0L, totalIncomes = 0L
            )
        }
    }

    private fun Raise<CategoryNotFound>.findCategorizedAmounts(query: Query): CategorizedAmounts {
        val categoryQuery = query.adjustSelect {
            select(
                Categories.id, Categories.income, Transactions.amount.sum(), Transactions.id.count()
            )
        }.adjustColumnSet { innerJoin(Categories, { Transactions.categoryId }, { Categories.id }) }
            .groupBy(Categories.id, Categories.income).orderBy(Transactions.amount.sum() to SortOrder.DESC)
        val incomeCategoriesAmounts = mutableListOf<CategoryAmount>()
        val expenseCategoriesAmounts = mutableListOf<CategoryAmount>()
        categoryQuery.forEach { row ->
            val categoryResponse = ensureNotNull(
                CategoryDao.findById(row[Categories.id])
            ) { CategoryNotFound }.toCategoryResponse()

            val amount = CategoryAmount(
                item = categoryResponse,
                sum = row[Transactions.amount.sum()] ?: 0L,
                count = row[Transactions.id.count()].toInt()
            )
            if (categoryResponse.income) incomeCategoriesAmounts.add(amount)
            else expenseCategoriesAmounts.add(amount)
        }

        return CategorizedAmounts(
            expenses = expenseCategoriesAmounts, incomes = incomeCategoriesAmounts
        )
    }

    private fun Raise<TagNotFound>.findTagsAmounts(query: Query): List<TagAmount> =
        query.adjustSelect {
            select(TransactionTags.tagId, Transactions.expenseSum, Transactions.incomeSum, Transactions.id.count())
        }
            .adjustColumnSet { innerJoin(TransactionTags, { Transactions.id }, { TransactionTags.transactionId }) }
            .groupBy(TransactionTags.tagId)
            .orderBy(Transactions.amount.sum() to SortOrder.DESC)
            .map { row ->
                TagAmount(
                    item = ensureNotNull(TagDao.findById(row[TransactionTags.tagId])) { TagNotFound }.toTagResponse(),
                    expenses = row[Transactions.expenseSum] ?: 0L,
                    incomes = row[Transactions.incomeSum] ?: 0,
                    count = row[Transactions.id.count()].toInt()
                )
            }

    private fun Raise<AccountNotFound>.findAccountAmounts(
        query: Query, ownerId: UUID
    ): List<AccountStatisticsResponse> {
        val accountIds = Accounts.select(Accounts.id).where(Accounts.ownerId eq ownerId).map { it[Accounts.id].value }
        val transferExpenseSum = transferExpenseSum(accountIds)
        val transferIncomeSum = transferIncomeSum(accountIds)
        val transferIncomesColumn = transferIncomeSum.alias("transfer_incomes")

        val transferIncomesQuery = query.copy().adjustSelect {
            select(
                Transactions.incomeAccountId, transferIncomesColumn
            )
        }.groupBy(Transactions.incomeAccountId).alias("transfer_incomes")
        return query.adjustColumnSet {
            leftJoin(
                transferIncomesQuery,
                { Transactions.sourceAccountId },
                { transferIncomesQuery[Transactions.incomeAccountId] })
        }.adjustSelect {
            select(
                Transactions.sourceAccountId,
                Transactions.expenseSum,
                Transactions.incomeSum,
                transferExpenseSum,
                transferIncomesQuery[transferIncomesColumn]
            )
        }.groupBy(Transactions.sourceAccountId).map { row ->
            val accountId = row[Transactions.sourceAccountId]
            val accountDao = ensureNotNull(AccountDao.findById(accountId)) { AccountNotFound }

            val expenses = row[Transactions.expenseSum] ?: 0L
            val incomes = row[Transactions.incomeSum] ?: 0L
            val transferExpenses = row[transferExpenseSum] ?: 0L
            val transferIncomes = row[transferIncomesQuery[transferIncomesColumn]] ?: 0L

            val allExpenses = expenses + transferExpenses
            val allIncomes = incomes + transferIncomes

            AccountStatisticsResponse(
                account = AccountResponse(
                    id = accountDao.id.value,
                    name = accountDao.name,
                    type = accountDao.type,
                    initialBalance = accountDao.initialBalance,
                    balance = accountDao.balance
                ), expenses = allExpenses, incomes = allIncomes, profit = allIncomes - allExpenses
            )
        }
    }
}