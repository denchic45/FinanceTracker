package com.denchic45.financetracker.transaction

import com.denchic45.financetracker.KtorClientTest
import com.denchic45.financetracker.account.AccountApi
import com.denchic45.financetracker.account.model.AccountRequest
import com.denchic45.financetracker.account.model.AccountType
import com.denchic45.financetracker.assertedLeft
import com.denchic45.financetracker.assertedNone
import com.denchic45.financetracker.assertedRight
import com.denchic45.financetracker.category.CategoryApi
import com.denchic45.financetracker.category.model.CreateCategoryRequest
import com.denchic45.financetracker.error.InvalidPageSize
import com.denchic45.financetracker.transaction.model.TransactionRequest
import com.denchic45.financetracker.transaction.model.TransactionResponse
import com.denchic45.financetracker.transaction.model.TransferTransactionRequest
import com.denchic45.financetracker.transaction.model.TransferTransactionResponse
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.properties.Delegates
import kotlin.test.assertTrue

class TransactionApiTest : KtorClientTest() {
    private val transactionApi: TransactionApi by inject { parametersOf(client) }
    private val accountApi: AccountApi by inject { parametersOf(client) }
    private val categoryApi: CategoryApi by inject { parametersOf(client) }

    // Fields to store IDs created in setup
    private lateinit var primaryAccountId: UUID
    private lateinit var secondaryAccountId: UUID
    private var categoryId by Delegates.notNull<Long>()

    private val initialPrimaryBalance = 50000L
    private val initialSecondaryBalance = 100000L

    @BeforeAll
    override fun beforeAll(): Unit = runBlocking {
        super.beforeAll()
        // Create Primary Account
        val createdPrimaryAccount = accountApi.create(
            AccountRequest(
                name = "Primary Account",
                type = AccountType.CARD,
                initialBalance = initialPrimaryBalance
            )
        ).assertedRight()
        primaryAccountId = createdPrimaryAccount.id

        // Create Secondary Account for transfers
        val createdSecondaryAccount = accountApi.create(
            AccountRequest(
                name = "Savings Account",
                type = AccountType.BILL,
                initialBalance = initialSecondaryBalance
            )
        ).assertedRight()
        secondaryAccountId = createdSecondaryAccount.id

        // Create Expense Category
        val createdCategory = categoryApi.create(
            CreateCategoryRequest(name = "Test Expense Category", icon = "test_icon", income = false)
        ).assertedRight()
        categoryId = createdCategory.id
    }

    @AfterAll
    override fun afterAll(): Unit = runBlocking {
        accountApi.delete(primaryAccountId).assertedNone()
        accountApi.delete(secondaryAccountId).assertedNone()
        categoryApi.delete(categoryId).assertedNone()
        super.afterAll()
    }


    @Test
    fun testCrud(): Unit = runBlocking {
        val createRequest = TransactionRequest(
            income = false,
            datetime = LocalDateTime(2025, 11, 1, 10, 0, 0),
            amount = 15000L,
            note = "Initial groceries expense",
            accountId = primaryAccountId,
            categoryId = categoryId
        )

        val createdTransaction = transactionApi.create(createRequest)
            .assertedRight() as TransactionResponse

        createdTransaction.apply {
            assertEquals(createRequest.amount, amount)
        }

        transactionApi.getById(createdTransaction.id).assertedRight()

        val updateRequest = TransactionRequest(
            income = false,
            datetime = LocalDateTime(2025, 11, 1, 11, 0, 0),
            amount = 20000L,
            note = "Updated travel expense",
            accountId = primaryAccountId,
            categoryId = categoryId
        )

        transactionApi.update(createdTransaction.id, updateRequest)
            .assertedRight()
            .apply {
                this as TransactionResponse
                assertEquals(updateRequest.amount, amount)
                assertEquals(primaryAccountId, account.id)
                assertEquals(categoryId, category.id)
            }

        // --- 4. Delete Transaction ---
        transactionApi.delete(createdTransaction.id).assertedNone()
    }

    /**
     * Test creating a TransferTransactionRequest, including balance checks.
     */
    @Test
    fun testCreateTransferTransaction(): Unit = runBlocking {
        // Initial balances set here for testing balance assertions
        val incomeAmount = 20000L
        val transferAmount = 50000L

        // --- 1. Create income transaction to primary account ---
        val incomeRequest = TransactionRequest(
            income = true,
            datetime = LocalDateTime(2025, 11, 2, 10, 0, 0),
            amount = incomeAmount,
            note = "Initial income for transfer test",
            accountId = primaryAccountId,
            categoryId = categoryId
        )
        val createdIncomeTx = transactionApi.create(incomeRequest).assertedRight() as TransactionResponse

        val balanceAfterIncome = initialPrimaryBalance + incomeAmount

        // --- 2. Request and assert balances BEFORE transfer ---
        // Assuming AccountResponse has a 'balance' field
        val primaryAccountBeforeTransfer = accountApi.getById(primaryAccountId).assertedRight()
        val secondaryAccountBeforeTransfer = accountApi.getById(secondaryAccountId).assertedRight()

        assertEquals(
            balanceAfterIncome,
            primaryAccountBeforeTransfer.balance,
            "Primary balance incorrect after income."
        )
        assertEquals(
            initialSecondaryBalance,
            secondaryAccountBeforeTransfer.balance,
            "Secondary balance incorrect initially."
        )

        // --- 3. Create transfer transaction from primary (source) to secondary (destination) ---
        val transferRequest = TransferTransactionRequest(
            datetime = LocalDateTime(2025, 11, 2, 15, 30, 0),
            amount = transferAmount,
            note = "Transfer to savings",
            accountId = primaryAccountId,      // Source
            incomeSourceId = secondaryAccountId // Destination
        )

        val createdTransferTx = transactionApi.create(transferRequest)
            .assertedRight() as TransferTransactionResponse

        // Expected balances AFTER transfer:
        val expectedPrimaryBalanceAfterTransfer = balanceAfterIncome - transferAmount // 70000 - 50000 = 20000
        val expectedSecondaryBalanceAfterTransfer = initialSecondaryBalance + transferAmount // 100000 + 50000 = 150000

        // Request both accounts again for checking them balances
        val primaryAccountAfterTransfer = accountApi.getById(primaryAccountId).assertedRight()
        val secondaryAccountAfterTransfer = accountApi.getById(secondaryAccountId).assertedRight()

        assertEquals(
            expectedPrimaryBalanceAfterTransfer,
            primaryAccountAfterTransfer.balance,
            "Primary balance incorrect after transfer."
        )
        assertEquals(
            expectedSecondaryBalanceAfterTransfer,
            secondaryAccountAfterTransfer.balance,
            "Secondary balance incorrect after transfer."
        )

        // Cleanup transactions
        transactionApi.delete(createdIncomeTx.id).assertedNone()
        transactionApi.delete(createdTransferTx.id).assertedNone()
    }

    /**
     * Test the getList functionality (paging).
     */
    @Test
    fun testPaging(): Unit = runBlocking {
        // Use buildList to create and collect transaction IDs
        val createdTransactionIds: List<Long> = buildList {
            // Pre-create 5 transactions for testing two pages with a size of 3
            for (i in 1..5) {
                val request = TransactionRequest(
                    income = false,
                    datetime = LocalDateTime(2025, 11, 5, 10, 0, i),
                    amount = (1000 * i).toLong(),
                    note = "Paging Test $i",
                    accountId = primaryAccountId,
                    categoryId = categoryId
                )
                val createdTx = transactionApi.create(request).assertedRight() as TransactionResponse
                add(createdTx.id)
            }
        }

        // Test page 1 with size 3 (Expected total pages: 2)
        val page1 = transactionApi.getList(page = 1, pageSize = 3).assertedRight()
        assertEquals(3, page1.results.size)
        assertEquals(1, page1.page)
        assertEquals(2, page1.totalPages)

        // Test page 2 with size 3 (Expected total pages: 2)
        val page2 = transactionApi.getList(page = 2, pageSize = 3).assertedRight()
        assertEquals(2, page2.results.size)
        assertEquals(2, page2.page)
        assertEquals(2, page2.totalPages)

        // Cleanup the transactions created in this specific test
        createdTransactionIds.forEach { transactionId ->
            transactionApi.delete(transactionId).assertedNone()
        }
    }

    /**
     * Test the constraint that pageSize cannot exceed 30.
     */
    @Test
    fun testPaging_MaxPageSizeError(): Unit = runBlocking {
        val maxPageSize = 30
        val invalidPageSize = 31

        val result = transactionApi.getList(page = 1, pageSize = invalidPageSize).assertedLeft()

        assertTrue(result is InvalidPageSize, "Expected InvalidPageSize error")

        assertEquals(maxPageSize, result.maxPageSize)
        assertEquals(invalidPageSize, result.actualPageSize)
    }
}