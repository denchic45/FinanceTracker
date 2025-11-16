package com.denchic45.financetracker.data.repository

import androidx.room.withTransaction
import arrow.core.Ior
import com.denchic45.financetracker.api.account.AccountApi
import com.denchic45.financetracker.api.category.CategoryApi
import com.denchic45.financetracker.api.category.model.CategoryResponse
import com.denchic45.financetracker.api.category.model.CreateCategoryRequest
import com.denchic45.financetracker.api.category.model.UpdateCategoryRequest
import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.dao.CategoryDao
import com.denchic45.financetracker.data.mapper.toAccountEntities
import com.denchic45.financetracker.data.mapper.toCategoryEntities
import com.denchic45.financetracker.data.mapper.toCategoryEntity
import com.denchic45.financetracker.data.mapper.toCategoryItem
import com.denchic45.financetracker.data.mapper.toCategoryItems
import com.denchic45.financetracker.data.observeData
import com.denchic45.financetracker.data.safeFetch
import com.denchic45.financetracker.data.safeFetchForEmptyResult
import com.denchic45.financetracker.domain.model.CategoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val categoryApi: CategoryApi,
    private val accountApi: AccountApi,
    private val database: AppDatabase
) {
    fun observe(income: Boolean) = observeData(
        query = categoryDao.observe(income).map { entities ->
            entities.toCategoryItems()
        },
        fetch = {
            categoryApi.getList(income).onRight {
                database.withTransaction {
                    categoryDao.upsert(it.toCategoryEntities())
                }
            }.onLeft { println("left: $it") }
        }
    )

    fun observeById(categoryId: Long): Flow<Ior<Failure, CategoryItem?>> {
        return observeData(
            query = categoryDao.observeById(categoryId).map { entity ->
                entity?.toCategoryItem()
            },
            fetch = {
                categoryApi.getById(categoryId)
                    .onRight { upsertCategory(it) }
            }
        )
    }

    suspend fun add(request: CreateCategoryRequest): RequestResult<CategoryResponse> {
        return safeFetch { categoryApi.create(request) }
            .onRight { response ->
                upsertCategory(response)
            }
    }

    suspend fun update(
        categoryId: Long,
        request: UpdateCategoryRequest
    ): RequestResult<CategoryResponse> {
        return safeFetch { categoryApi.update(categoryId, request) }
            .onRight { response -> upsertCategory(response) }
    }

    private suspend fun upsertCategory(response: CategoryResponse) {
        database.withTransaction {
            categoryDao.upsert(response.toCategoryEntity())
        }
    }

    suspend fun remove(categoryId: Long): EmptyRequestResult {
        return safeFetchForEmptyResult { categoryApi.delete(categoryId) }.onNone {
            accountApi.getList().onRight {
                database.withTransaction {
                    accountDao.upsert(it.toAccountEntities())
                    categoryDao.deleteById(categoryId)
                }
            }
        }
    }
}