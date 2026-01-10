package com.denchic45.financetracker.api.category

import com.denchic45.financetracker.api.assertedNone
import com.denchic45.financetracker.api.assertedRight
import com.denchic45.financetracker.api.KtorClientTest
import com.denchic45.financetracker.api.category.model.CreateCategoryRequest
import com.denchic45.financetracker.api.category.model.UpdateCategoryRequest
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
            iconName = "money_bag",
            income = true
        )
        val createdCategory = categoryApi.create(request).assertedRight().apply {
            assertEquals(request.name, name)
            assertEquals(request.iconName, iconName)
            assertEquals(request.income, income)
        }

        categoryApi.getById(createdCategory.id).assertedRight().apply {
            assertEquals(createdCategory.id, id)
        }

        categoryApi.update(createdCategory.id, UpdateCategoryRequest("Salary", "money_bill"))
            .assertedRight().apply {
                assertEquals("Salary", name)
                assertEquals("money_bill", iconName)
            }

        categoryApi.delete(createdCategory.id).assertedNone()
    }
}