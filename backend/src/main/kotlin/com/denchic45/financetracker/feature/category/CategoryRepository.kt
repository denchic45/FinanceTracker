package com.denchic45.financetracker.feature.category

import arrow.core.*
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.denchic45.financetracker.api.category.model.CategoryResponse
import com.denchic45.financetracker.api.category.model.CreateCategoryRequest
import com.denchic45.financetracker.api.category.model.UpdateCategoryRequest
import com.denchic45.financetracker.api.error.CategoryNotFound
import com.denchic45.financetracker.api.error.UserNotFound
import com.denchic45.financetracker.database.table.Categories
import com.denchic45.financetracker.database.table.CategoryDao
import com.denchic45.financetracker.database.table.UserDao
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CategoryRepository() {

    fun add(request: CreateCategoryRequest, ownerId: UUID) = either {
        transaction {
            CategoryDao.new {
                name = request.name
                icon = request.icon
                income = request.income
                owner = ensureNotNull(UserDao.findById(ownerId)) { UserNotFound }
            }.toCategoryResponse()
        }
    }

    fun findById(categoryId: Long): Either<CategoryNotFound, CategoryResponse> = transaction {
        CategoryDao.findById(categoryId)?.toCategoryResponse()?.right() ?: CategoryNotFound.left()
    }

    fun findByType(income: Boolean, ownerId: UUID) = transaction {
        CategoryDao.find(Categories.income eq income and (Categories.ownerId eq ownerId)).toCategoryResponses()
    }

    fun update(categoryId: Long, request: UpdateCategoryRequest): Either<CategoryNotFound, CategoryResponse> =
        transaction {
            CategoryDao.findById(categoryId)?.apply {
                name = request.name
                icon = request.icon
            }?.toCategoryResponse()?.right() ?: CategoryNotFound.left()
        }

    fun remove(categoryId: Long): Option<CategoryNotFound> = transaction {
        CategoryDao.findById(categoryId)?.delete() ?: return@transaction CategoryNotFound.some()
        none()
    }

    fun addDefaultsFor(userId: UUID) {
        val defaultCategoryData = listOf(
            // --- EXPENSES ---
            Triple("Еда и Напитки", "burger", false),
            Triple("Транспорт", "car", false),
            Triple("Жилье и Аренда", "home", false),
            Triple("Развлечения", "video", false),
            Triple("Покупки", "shopping_cart", false),
            Triple("Одежда", "shirt", false),
            Triple("Здоровье", "first_aid_kit", false),
            Triple("Фитнес", "barbell", false),
            Triple("Подарки", "gift", false), // Расход
            Triple("Изучение английского", "language", false),
            Triple("Интернет", "access_point", false),
            Triple("Коммунальные Услуги", "umbrella", false),
            Triple("Прочие Расходы", "axe", false),

            // --- INCOMES ---
            Triple("Зарплата", "pig_money", true),
            Triple("Инвестиции", "building_bank", true),
            Triple("Подарки", "gift", true)
        )

        transaction {
            defaultCategoryData.forEach { (name, icon, isIncome) ->
                // Создание новой сущности CategoryDao для вставки в базу данных
                CategoryDao.new {
                    this.name = name
                    this.icon = icon
                    this.income = isIncome
                    this.owner = UserDao[userId]
                }
            }
        }
    }
}