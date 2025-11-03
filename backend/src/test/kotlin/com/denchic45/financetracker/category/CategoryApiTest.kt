package com.denchic45.com.denchic45.financetracker.category

import com.denchic45.financetracker.assertedNone
import com.denchic45.financetracker.assertedRight
import com.denchic45.financetracker.KtorClientTest
import com.denchic45.financetracker.category.CategoryApi
import com.denchic45.financetracker.category.model.CreateCategoryRequest
import com.denchic45.financetracker.category.model.UpdateCategoryRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class CategoryApiTest : KtorClientTest() {
    private val categoryApi: CategoryApi by inject { parametersOf(client) }

    @Test
    fun testCrud(): Unit = runBlocking {
        val request = CreateCategoryRequest(
            name = "Monthly Salary",
            icon = "money_bag",
            income = true
        )
        val createdCategory = categoryApi.create(request).assertedRight().apply {
            assertEquals(request.name, name)
            assertEquals(request.icon, icon)
            assertEquals(request.income, income)
        }

        categoryApi.getById(createdCategory.id).assertedRight().apply {
            assertEquals(createdCategory.id, id)
        }

        categoryApi.update(createdCategory.id, UpdateCategoryRequest("Salary", "money_bill"))
            .assertedRight().apply {
                assertEquals("Salary", name)
                assertEquals("money_bill", icon)
            }

        categoryApi.delete(createdCategory.id).assertedNone()
    }
}