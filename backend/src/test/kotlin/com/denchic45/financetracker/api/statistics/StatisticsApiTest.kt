package com.denchic45.financetracker.api.statistics

import com.denchic45.financetracker.api.KtorClientTest
import com.denchic45.financetracker.api.account.AccountApi
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.api.account.model.CreateAccountRequest
import com.denchic45.financetracker.api.assertedNone
import com.denchic45.financetracker.api.assertedRight
import com.denchic45.financetracker.api.category.CategoryApi
import com.denchic45.financetracker.api.category.model.CreateCategoryRequest
import com.denchic45.financetracker.api.statistic.StatisticsApi
import com.denchic45.financetracker.api.statistic.model.GroupingPeriod
import com.denchic45.financetracker.api.statistic.model.StatisticsField
import com.denchic45.financetracker.api.tag.TagApi
import com.denchic45.financetracker.api.tag.model.TagRequest
import com.denchic45.financetracker.api.transaction.TransactionApi
import com.denchic45.financetracker.api.transaction.model.TransactionRequest
import com.denchic45.financetracker.api.transaction.model.TransferTransactionRequest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.properties.Delegates
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StatisticsApiTest : KtorClientTest() {

    private val statisticsApi: StatisticsApi by inject { parametersOf(client) }
    private val accountApi: AccountApi by inject { parametersOf(client) }
    private val categoryApi: CategoryApi by inject { parametersOf(client) }
    private val transactionApi: TransactionApi by inject { parametersOf(client) }
    private val tagApi: TagApi by inject { parametersOf(client) }

    private lateinit var primaryAccountId: UUID
    private lateinit var secondaryAccountId: UUID
    private var incomeCategoryId by Delegates.notNull<Long>()
    private var expenseCategoryId by Delegates.notNull<Long>()

    private val initialPrimaryBalance = 100000L
    private val initialSecondaryBalance = 50000L
    private val transactionsToDelete = mutableListOf<Long>()


    @BeforeAll
    override fun beforeAll(): Unit = runBlocking {
        super.beforeAll()
        // Create Accounts
        primaryAccountId = accountApi.create(
            CreateAccountRequest(
                name = "Primary Account",
                type = AccountType.BILL,
                initialBalance = initialPrimaryBalance
            )
        ).assertedRight().id

        secondaryAccountId = accountApi.create(
            CreateAccountRequest(
                name = "Secondary Account", type = AccountType.CASH, initialBalance = initialSecondaryBalance
            )
        ).assertedRight().id

        // Create Categories
        incomeCategoryId = categoryApi.create(
            CreateCategoryRequest(name = "Income Salary", icon = "income_icon", income = true)
        ).assertedRight().id

        expenseCategoryId = categoryApi.create(
            CreateCategoryRequest(name = "Expense Groceries", icon = "expense_icon", income = false)
        ).assertedRight().id
    }

    @AfterAll
    override fun afterAll(): Unit = runBlocking {
        // Delete Accounts and Categories
        accountApi.delete(primaryAccountId).assertedNone()
        accountApi.delete(secondaryAccountId).assertedNone()
        categoryApi.delete(incomeCategoryId).assertedNone()
        categoryApi.delete(expenseCategoryId).assertedNone()
        super.afterAll()
    }

    @AfterEach
    fun teardown(): Unit = runBlocking {
        // Delete all transactions created during the test
        transactionsToDelete.forEach { transactionId ->
            transactionApi.delete(transactionId).assertedNone()
        }
        transactionsToDelete.clear()
    }

    @Test
    fun testTotals(): Unit = runBlocking {
        val startDate = LocalDate(2025, 11, 3)
        val endDate = LocalDate(2025, 11, 9)

        // --- 1. Create Transactions over a week (Nov 3 to Nov 9) ---

        // Income 1: Day 1, Primary Account: +20000
        val income1Amount = 20000L
        transactionApi.create(
            TransactionRequest(
                income = true,
                datetime = startDate.atTime(10, 0),
                amount = income1Amount,
                note = "Weekly Salary deposit",
                accountId = primaryAccountId,
                categoryId = incomeCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Expense 1: Day 2, Primary Account: -5000
        val expense1Amount = 5000L
        transactionApi.create(
            TransactionRequest(
                income = false,
                datetime = startDate.plus(1, DateTimeUnit.DAY).atTime(11, 0),
                amount = expense1Amount,
                note = "Lunch and coffee",
                accountId = primaryAccountId,
                categoryId = expenseCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Transfer 1: Day 3, Primary to Secondary: -10000 (Transfer is excluded from totals)
        val transferAmount = 10000L
        transactionApi.create(
            TransferTransactionRequest(
                datetime = startDate.plus(2, DateTimeUnit.DAY).atTime(12, 0),
                amount = transferAmount,
                note = "Transfer to savings account",
                accountId = primaryAccountId,
                incomeSourceId = secondaryAccountId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Expense 2: Day 4, Secondary Account: -2000
        val expense2Amount = 2000L
        transactionApi.create(
            TransactionRequest(
                income = false,
                datetime = startDate.plus(3, DateTimeUnit.DAY).atTime(13, 0),
                amount = expense2Amount,
                note = "Movie tickets",
                accountId = secondaryAccountId,
                categoryId = expenseCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // --- 2. Calculate Expected Results ---

        // Final Balances
        val expectedPrimaryBalance = initialPrimaryBalance + income1Amount - expense1Amount - transferAmount
        val expectedSecondaryBalance = initialSecondaryBalance + transferAmount - expense2Amount

        // Statistics Totals (Transfers are typically excluded from Income/Expense/Net totals)
        val expectedIncomes = income1Amount
        val expectedExpenses = expense1Amount + expense2Amount
        val expectedProfit = expectedIncomes - expectedExpenses

        // --- 3. Get Statistics and Check Response ---
        val statsResponse = statisticsApi.getStatistics(
            fromDate = startDate, toDate = endDate, fields = listOf(StatisticsField.TOTALS)
        ).assertedRight()

        // Assert Totals (using the new field names: expenses, incomes, profit)
        val totals = statsResponse.totals
        assertEquals(expectedIncomes, totals?.incomes, "Total Incomes mismatch.")
        assertEquals(expectedExpenses, totals?.expenses, "Total Expenses mismatch.")
        assertEquals(expectedProfit, totals?.profit, "Profit/Net Amount mismatch.")

        // --- 4. Check Account Balances ---
        val primaryAccount = accountApi.getById(primaryAccountId).assertedRight()
        val secondaryAccount = accountApi.getById(secondaryAccountId).assertedRight()

        assertEquals(expectedPrimaryBalance, primaryAccount.balance, "Final Primary Account balance mismatch.")
        assertEquals(expectedSecondaryBalance, secondaryAccount.balance, "Final Secondary Account balance mismatch.")
    }

    @Test
    fun testGroupedAmounts(): Unit = runBlocking {
        val startDate = LocalDate(2025, 1, 1)
        val endDate = LocalDate(2025, 12, 31)

        // --- 1. Create Transactions across the year ---

        // List of transactions: (Date, Amount, isIncome)
        val transactionDates = listOf(
            // Jan
            Triple(LocalDate(2025, 1, 1), 10000L, true),  // T1: Day 1 Income
            Triple(LocalDate(2025, 1, 5), 2000L, false),  // T2: Day 5 Expense
            Triple(LocalDate(2025, 1, 8), 3000L, false),  // T3: Day 8 Expense
            // Feb
            Triple(LocalDate(2025, 2, 15), 50000L, true), // T4: Mid Feb Income
            Triple(LocalDate(2025, 2, 20), 10000L, false),// T5: Late Feb Expense (EXCLUDED from 50-day range)
            // Jun
            Triple(LocalDate(2025, 6, 1), 5000L, true),   // T6: Jun Income
            // Dec
            Triple(LocalDate(2025, 12, 31), 1000L, false) // T7: Dec Expense
        )

        transactionDates.forEach { (date, amount, isIncome) ->
            transactionApi.create(
                TransactionRequest(
                    income = isIncome,
                    datetime = date.atTime(12, 0),
                    amount = amount,
                    note = "Test ${if (isIncome) "Income" else "Expense"} on $date",
                    accountId = if (isIncome) primaryAccountId else secondaryAccountId,
                    categoryId = if (isIncome) incomeCategoryId else expenseCategoryId
                )
            ).assertedRight().let { transactionsToDelete.add(it.id) }
        }

        // --- 2. Scenario 1: 10 days grouped by DAY (Jan 1 to Jan 10) ---
        val dayStartDate = startDate
        val dayEndDate = startDate.plus(9, DateTimeUnit.DAY) // Jan 10

        val dayStats = statisticsApi.getStatistics(
            fromDate = dayStartDate,
            toDate = dayEndDate,
            fields = listOf(StatisticsField.GROUPED),
            groupBy = GroupingPeriod.DAY
        ).assertedRight()

        val dayGrouped = dayStats.groupedAmounts
        assertNotNull(dayGrouped, "Day grouped amounts must not be null.")
        assertEquals(10, dayGrouped.size, "Day grouped list must contain 10 days.")

        val dayMap = dayGrouped.associateBy { it.periodLabel }

        // Check specific days: Jan 1, 5, 8
        assertEquals(10000L, dayMap[LocalDate(2025, 1, 1).toString()]?.totalIncomes, "Jan 1 Income mismatch.")
        assertEquals(2000L, dayMap[LocalDate(2025, 1, 5).toString()]?.totalExpenses, "Jan 5 Expense mismatch.")
        assertEquals(3000L, dayMap[LocalDate(2025, 1, 8).toString()]?.totalExpenses, "Jan 8 Expense mismatch.")
        // Check an empty day
        assertEquals(0L, dayMap[LocalDate(2025, 1, 2).toString()]?.totalIncomes, "Jan 2 Income must be 0.")
        assertEquals(0L, dayMap[LocalDate(2025, 1, 2).toString()]?.totalExpenses, "Jan 2 Expense must be 0.")

        // --- 3. Scenario 2: 50 days grouped by WEEK (Jan 1 to Feb 19, 2025) ---
        val weekStartDate = startDate // Jan 1
        val weekEndDate = startDate.plus(49, DateTimeUnit.DAY) // Feb 19

        val weekStats = statisticsApi.getStatistics(
            fromDate = weekStartDate,
            toDate = weekEndDate,
            fields = listOf(StatisticsField.GROUPED),
            groupBy = GroupingPeriod.WEEK
        ).assertedRight()

        val weekGrouped = weekStats.groupedAmounts
        assertNotNull(weekGrouped, "Week grouped amounts must not be null.")

        // T1 (Jan 1): 10000L Income
        // T2 (Jan 5): 2000L Expense
        // T3 (Jan 8): 3000L Expense
        // T4 (Feb 15): 50000L Income
        // T5 (Feb 20): 10000L Expense -> EXCLUDED as it is outside the range ending Feb 19
        val expectedWeekIncomes = 10000L + 50000L // T1 + T4
        val expectedWeekExpenses = 2000L + 3000L // T2 + T3

        val actualWeekIncomes = weekGrouped.sumOf { it.totalIncomes }
        val actualWeekExpenses = weekGrouped.sumOf { it.totalExpenses }

        assertEquals(expectedWeekIncomes, actualWeekIncomes, "Total Incomes across weeks mismatch.")
        assertEquals(
            expectedWeekExpenses,
            actualWeekExpenses,
            "Total Expenses across weeks mismatch. T5 (Feb 20) must be excluded."
        )
        // Jan 1 to Feb 19 (50 days) generally results in 8 periods.
        assertEquals(8, weekGrouped.size, "Week grouped list must contain 8 periods (Jan 1 to Feb 19).")

        // --- 4. Scenario 3: Full year grouped by MONTH (Jan 1 to Dec 31) ---
        val monthStats = statisticsApi.getStatistics(
            fromDate = startDate,
            toDate = endDate,
            fields = listOf(StatisticsField.GROUPED),
            groupBy = GroupingPeriod.MONTH
        ).assertedRight()

        val monthGrouped = monthStats.groupedAmounts
        assertNotNull(monthGrouped, "Month grouped amounts must not be null.")
        assertEquals(12, monthGrouped.size, "Month grouped list must contain 12 months.")

        val monthMap = monthGrouped.associateBy { it.periodLabel }

        // Check specific months: Jan, Feb, Jun, Dec
        assertEquals(10000L, monthMap["2025-01"]?.totalIncomes, "Jan Incomes mismatch.")
        assertEquals(5000L, monthMap["2025-01"]?.totalExpenses, "Jan Expenses mismatch (2000+3000).")

        assertEquals(50000L, monthMap["2025-02"]?.totalIncomes, "Feb Incomes mismatch.")
        assertEquals(10000L, monthMap["2025-02"]?.totalExpenses, "Feb Expenses mismatch (T5 included).")

        assertEquals(5000L, monthMap["2025-06"]?.totalIncomes, "Jun Incomes mismatch.")
        assertEquals(0L, monthMap["2025-06"]?.totalExpenses, "Jun Expenses mismatch.")

        assertEquals(0L, monthMap["2025-12"]?.totalIncomes, "Dec Incomes mismatch.")
        assertEquals(1000L, monthMap["2025-12"]?.totalExpenses, "Dec Expenses mismatch.")

        // Check an empty month (e.g., Mar)
        assertEquals(0L, monthMap["2025-03"]?.totalIncomes, "Mar Incomes must be 0.")
        assertEquals(0L, monthMap["2025-03"]?.totalExpenses, "Mar Expenses must be 0.")

        // --- 5. Scenario 4: Full period grouped by YEAR (Jan 1 to Dec 31) ---
        val yearStats = statisticsApi.getStatistics(
            fromDate = startDate,
            toDate = endDate,
            fields = listOf(StatisticsField.GROUPED),
            groupBy = GroupingPeriod.YEAR
        ).assertedRight()

        val yearGrouped = yearStats.groupedAmounts
        assertNotNull(yearGrouped, "Year grouped amounts must not be null.")
        assertEquals(1, yearGrouped.size, "Year grouped list must contain 1 year period.")

        val yearData = yearGrouped.first()
        val expectedYearIncomes = 10000L + 50000L + 5000L // T1 + T4 + T6
        val expectedYearExpenses = 2000L + 3000L + 10000L + 1000L // T2 + T3 + T5 + T7

        assertEquals("2025", yearData.periodLabel, "Year label mismatch.")
        assertEquals(expectedYearIncomes, yearData.totalIncomes, "Year Incomes mismatch.")
        assertEquals(expectedYearExpenses, yearData.totalExpenses, "Year Expenses mismatch.")
    }

    @Test
    fun testCategorizedAmounts(): Unit = runBlocking {
        val startDate = LocalDate(2025, 11, 3)
        val endDate = LocalDate(2025, 11, 9)

        // --- 1. Create Transactions ---

        // Income 1: Day 1, Primary Account: +20000 (Uses incomeCategoryId)
        val income1Amount = 20000L
        transactionApi.create(
            TransactionRequest(
                income = true,
                datetime = startDate.atTime(10, 0),
                amount = income1Amount,
                note = "Weekly Salary deposit",
                accountId = primaryAccountId,
                categoryId = incomeCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Expense 1: Day 2, Primary Account: -5000 (Uses expenseCategoryId)
        val expense1Amount = 5000L
        transactionApi.create(
            TransactionRequest(
                income = false,
                datetime = startDate.plus(1, DateTimeUnit.DAY).atTime(11, 0),
                amount = expense1Amount,
                note = "Lunch and coffee",
                accountId = primaryAccountId,
                categoryId = expenseCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Transfer 1: Day 3, Primary to Secondary: -10000 (Transfers are excluded from categoriesAmounts)
        val transferAmount = 10000L
        transactionApi.create(
            TransferTransactionRequest(
                datetime = startDate.plus(2, DateTimeUnit.DAY).atTime(12, 0),
                amount = transferAmount,
                note = "Transfer to savings account",
                accountId = primaryAccountId,
                incomeSourceId = secondaryAccountId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Expense 2: Day 4, Secondary Account: -2000 (Uses expenseCategoryId)
        val expense2Amount = 2000L
        transactionApi.create(
            TransactionRequest(
                income = false,
                datetime = startDate.plus(3, DateTimeUnit.DAY).atTime(13, 0),
                amount = expense2Amount,
                note = "Movie tickets",
                accountId = secondaryAccountId,
                categoryId = expenseCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // --- 2. Define Expected Categorized Amounts ---
        val expectedTotalIncome = income1Amount
        val expectedIncomeCount = 1

        val expectedTotalExpense = expense1Amount + expense2Amount
        val expectedExpenseCount = 2

        // --- 3. Get Statistics and Check Response ---
        val statsResponse = statisticsApi.getStatistics(
            fromDate = startDate, toDate = endDate, fields = listOf(StatisticsField.CATEGORIES)
        ).assertedRight()

        val categorizedAmounts = statsResponse.categoriesAmounts
        assertNotNull(categorizedAmounts, "Categorized amounts must not be null.")

        // Assert Income Category results
        val incomeStats = categorizedAmounts.incomes.find { it.item.id == incomeCategoryId }
        assertNotNull(incomeStats, "Statistics for Income Category not found.")
        incomeStats.let {
            assertEquals(expectedTotalIncome, it.sum, "Income Category sum mismatch.")
            assertEquals(expectedIncomeCount, it.count, "Income Category count mismatch.")
        }

        // Assert Expense Category results
        val expenseStats = categorizedAmounts.expenses.find { it.item.id == expenseCategoryId }
        assertNotNull(expenseStats, "Statistics for Expense Category not found.")
        expenseStats.let {
            assertEquals(expectedTotalExpense, it.sum, "Expense Category sum mismatch.")
            assertEquals(expectedExpenseCount, it.count, "Expense Category count mismatch.")
        }
    }

    @Test
    fun testTagsAmounts(): Unit = runBlocking {
        val tagIds: List<Long> = listOf(
            tagApi.create(TagRequest(name = "Personal")).assertedRight().id,
            tagApi.create(TagRequest(name = "Work")).assertedRight().id,
            tagApi.create(TagRequest(name = "Travel")).assertedRight().id,
            tagApi.create(TagRequest(name = "Home")).assertedRight().id,
            tagApi.create(TagRequest(name = "Shopping")).assertedRight().id
        )
        val startDate = LocalDate(2025, 11, 3)
        val endDate = LocalDate(2025, 11, 9)

        // Destructure the list of tag IDs for easy reference
        val (tag1Id, tag2Id, tag3Id, tag4Id, tag5Id) = tagIds

        // --- 1. Create Transactions with 5 Tags ---

        // T1 (Income): +20000, Tag: Personal (tag1)
        val income1Amount = 20000L
        transactionApi.create(
            TransactionRequest(
                income = true, datetime = startDate.atTime(10, 0), amount = income1Amount,
                note = "Tagged Salary (T1)",
                accountId = primaryAccountId, categoryId = incomeCategoryId,
                tagIds = listOf(tag1Id)
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // T2 (Expense): -5000, Tag: Work (tag2)
        val expense1Amount = 5000L
        transactionApi.create(
            TransactionRequest(
                income = false, datetime = startDate.plus(1, DateTimeUnit.DAY).atTime(11, 0), amount = expense1Amount,
                note = "Work Expense (T2)",
                accountId = primaryAccountId, categoryId = expenseCategoryId,
                tagIds = listOf(tag2Id)
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // T3 (Expense): -2000, Tags: Personal (tag1) + Work (tag2) + Travel (tag3)
        val expense2Amount = 2000L
        transactionApi.create(
            TransactionRequest(
                income = false, datetime = startDate.plus(3, DateTimeUnit.DAY).atTime(13, 0), amount = expense2Amount,
                note = "Dual/Triple Tag Expense (T3)",
                accountId = secondaryAccountId, categoryId = expenseCategoryId,
                tagIds = listOf(tag1Id, tag2Id, tag3Id)
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // T4 (Income): +1000, Tags: Travel (tag3) + Home (tag4)
        val income2Amount = 1000L
        transactionApi.create(
            TransactionRequest(
                income = true, datetime = startDate.plus(4, DateTimeUnit.DAY).atTime(14, 0), amount = income2Amount,
                note = "Small Home Income (T4)",
                accountId = primaryAccountId, categoryId = incomeCategoryId,
                tagIds = listOf(tag3Id, tag4Id)
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // T5 (Expense): -7000, Tag: Shopping (tag5)
        val expense3Amount = 7000L
        transactionApi.create(
            TransactionRequest(
                income = false, datetime = startDate.plus(5, DateTimeUnit.DAY).atTime(15, 0), amount = expense3Amount,
                note = "Shopping Expense (T5)",
                accountId = secondaryAccountId, categoryId = expenseCategoryId,
                tagIds = listOf(tag5Id)
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Transfer: Should be excluded from Tag statistics.
        transactionApi.create(
            TransferTransactionRequest(
                datetime = startDate.plus(2, DateTimeUnit.DAY).atTime(12, 0), amount = 10000L,
                note = "Transfer to savings account",
                accountId = primaryAccountId, incomeSourceId = secondaryAccountId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }


        // --- 2. Define Expected Tag Amounts (Total sum of incomes/expenses for tagged transactions) ---

        // Tag 1 (Personal): T1 (Income 20000, Expense 0) + T3 (Income 0, Expense 2000)
        val expectedTag1Incomes = income1Amount
        val expectedTag1Expenses = expense2Amount
        val expectedTag1Count = 2

        // Tag 2 (Work): T2 (Income 0, Expense 5000) + T3 (Income 0, Expense 2000)
        val expectedTag2Incomes = 0L
        val expectedTag2Expenses = expense1Amount + expense2Amount
        val expectedTag2Count = 2

        // Tag 3 (Travel): T3 (Income 0, Expense 2000) + T4 (Income 1000, Expense 0)
        val expectedTag3Incomes = income2Amount
        val expectedTag3Expenses = expense2Amount
        val expectedTag3Count = 2

        // Tag 4 (Home): T4 (Income 1000, Expense 0)
        val expectedTag4Incomes = income2Amount
        val expectedTag4Expenses = 0L
        val expectedTag4Count = 1

        // Tag 5 (Shopping): T5 (Income 0, Expense 7000)
        val expectedTag5Incomes = 0L
        val expectedTag5Expenses = expense3Amount
        val expectedTag5Count = 1

        // --- 3. Get Statistics and Check Response ---
        val statsResponse = statisticsApi.getStatistics(
            fromDate = startDate,
            toDate = endDate,
            fields = listOf(StatisticsField.TAGS)
        ).assertedRight()

        val tagsAmounts = statsResponse.tagsAmounts
        assertNotNull(tagsAmounts, "Tags amounts must not be null.")
        assertEquals(5, tagsAmounts.size, "Tags amounts list must contain 5 entries.")

        val tag1Stats = tagsAmounts.find { it.item.id == tag1Id }
        val tag2Stats = tagsAmounts.find { it.item.id == tag2Id }
        val tag3Stats = tagsAmounts.find { it.item.id == tag3Id }
        val tag4Stats = tagsAmounts.find { it.item.id == tag4Id }
        val tag5Stats = tagsAmounts.find { it.item.id == tag5Id }

        assertNotNull(tag1Stats, "Statistics for Tag 1 not found.")
        assertNotNull(tag2Stats, "Statistics for Tag 2 not found.")
        assertNotNull(tag3Stats, "Statistics for Tag 3 not found.")
        assertNotNull(tag4Stats, "Statistics for Tag 4 not found.")
        assertNotNull(tag5Stats, "Statistics for Tag 5 not found.")

        // Assert Tag 1 results (Personal)
        tag1Stats.let {
            assertEquals(expectedTag1Incomes, it.incomes, "Tag 1 ('Personal') incomes mismatch.")
            assertEquals(expectedTag1Expenses, it.expenses, "Tag 1 ('Personal') expenses mismatch.")
            assertEquals(expectedTag1Count, it.count, "Tag 1 ('Personal') count mismatch.")
        }

        // Assert Tag 2 results (Work)
        tag2Stats.let {
            assertEquals(expectedTag2Incomes, it.incomes, "Tag 2 ('Work') incomes mismatch.")
            assertEquals(expectedTag2Expenses, it.expenses, "Tag 2 ('Work') expenses mismatch.")
            assertEquals(expectedTag2Count, it.count, "Tag 2 ('Work') count mismatch.")
        }

        // Assert Tag 3 results (Travel)
        tag3Stats.let {
            assertEquals(expectedTag3Incomes, it.incomes, "Tag 3 ('Travel') incomes mismatch.")
            assertEquals(expectedTag3Expenses, it.expenses, "Tag 3 ('Travel') expenses mismatch.")
            assertEquals(expectedTag3Count, it.count, "Tag 3 ('Travel') count mismatch.")
        }

        // Assert Tag 4 results (Home)
        tag4Stats.let {
            assertEquals(expectedTag4Incomes, it.incomes, "Tag 4 ('Home') incomes mismatch.")
            assertEquals(expectedTag4Expenses, it.expenses, "Tag 4 ('Home') expenses mismatch.")
            assertEquals(expectedTag4Count, it.count, "Tag 4 ('Home') count mismatch.")
        }

        // Assert Tag 5 results (Shopping)
        tag5Stats.let {
            assertEquals(expectedTag5Incomes, it.incomes, "Tag 5 ('Shopping') incomes mismatch.")
            assertEquals(expectedTag5Expenses, it.expenses, "Tag 5 ('Shopping') expenses mismatch.")
            assertEquals(expectedTag5Count, it.count, "Tag 5 ('Shopping') count mismatch.")
        }


        tagIds.forEach { tagId ->
            tagApi.delete(tagId).assertedNone()
        }
    }

    @Test
    fun testAccountsStatistics(): Unit = runBlocking {
        val startDate = LocalDate(2025, 11, 3)
        val endDate = LocalDate(2025, 11, 9)

        // --- 1. Create Transactions over a week (Same transactions as testTotals) ---

        // Income 1: Day 1, Primary Account: +20000
        val incomeToPrimaryAccount = 20000L
        transactionApi.create(
            TransactionRequest(
                income = true,
                datetime = startDate.atTime(10, 0),
                amount = incomeToPrimaryAccount,
                note = "Weekly Salary deposit",
                accountId = primaryAccountId,
                categoryId = incomeCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Expense 1: Day 2, Primary Account: -5000
        val expenseFromPrimaryToSecondaryAccount = 5000L
        transactionApi.create(
            TransactionRequest(
                income = false,
                datetime = startDate.plus(1, DateTimeUnit.DAY).atTime(11, 0),
                amount = expenseFromPrimaryToSecondaryAccount,
                note = "Lunch and coffee",
                accountId = primaryAccountId,
                categoryId = expenseCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Transfer 1: Day 3, Primary to Secondary: -10000 (Internal transfer)
        val transferFromPrimaryToSecondaryAccount = 10000L
        transactionApi.create(
            TransferTransactionRequest(
                datetime = startDate.plus(2, DateTimeUnit.DAY).atTime(12, 0),
                amount = transferFromPrimaryToSecondaryAccount,
                note = "Transfer to savings account",
                accountId = primaryAccountId,
                incomeSourceId = secondaryAccountId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }

        // Expense 2: Day 4, Secondary Account: -2000
        val expenseFromSecondaryAccount = 2000L
        transactionApi.create(
            TransactionRequest(
                income = false,
                datetime = startDate.plus(3, DateTimeUnit.DAY).atTime(13, 0),
                amount = expenseFromSecondaryAccount,
                note = "Movie tickets",
                accountId = secondaryAccountId,
                categoryId = expenseCategoryId
            )
        ).assertedRight().let { transactionsToDelete.add(it.id) }


        // --- 2. Calculate Expected Account-Specific Statistics ---

        // Primary Account (CARD)
        val expectedPrimaryIncomes = incomeToPrimaryAccount
        val expectedPrimaryExpenses = expenseFromPrimaryToSecondaryAccount + transferFromPrimaryToSecondaryAccount
        val expectedPrimaryProfit = expectedPrimaryIncomes - expectedPrimaryExpenses

        // Secondary Account (CASH)
        val expectedSecondaryIncomes = transferFromPrimaryToSecondaryAccount
        val expectedSecondaryExpenses = expenseFromSecondaryAccount
        val expectedSecondaryProfit = expectedSecondaryIncomes - expectedSecondaryExpenses

        // --- 3. Get Statistics and Check Response ---
        val statsResponse = statisticsApi.getStatistics(
            fromDate = startDate, toDate = endDate, fields = listOf(StatisticsField.ACCOUNTS)
        ).assertedRight()

        // Assert Accounts list is present and contains 2 entries
        val accountsStats = statsResponse.accounts
        assertNotNull(accountsStats, "Accounts statistics list must not be null.")
        assertEquals(2, accountsStats.size, "Accounts statistics list must contain 2 entries.")

        val primaryStats = accountsStats.find {
            // The AccountResponse in the mock has only ID, so we use it for checking
            // In a real scenario, we'd check account.id
            it.account.id == primaryAccountId
        }
        val secondaryStats = accountsStats.find {
            it.account.id == secondaryAccountId
        }

        assertNotNull(primaryStats, "Statistics for Primary Account not found.")
        assertNotNull(secondaryStats, "Statistics for Secondary Account not found.")

        // Assert Primary Account Statistics
        primaryStats.let {
            assertEquals(expectedPrimaryIncomes, it.incomes, "Primary Account Incomes mismatch.")
            assertEquals(expectedPrimaryExpenses, it.expenses, "Primary Account Expenses mismatch.")
            assertEquals(expectedPrimaryProfit, it.profit, "Primary Account Profit mismatch.")
        }

        // Assert Secondary Account Statistics
        secondaryStats.let {
            assertEquals(expectedSecondaryIncomes, it.incomes, "Secondary Account Incomes mismatch.")
            assertEquals(expectedSecondaryExpenses, it.expenses, "Secondary Account Expenses mismatch.")
            assertEquals(expectedSecondaryProfit, it.profit, "Secondary Account Profit mismatch.")
        }
    }
}