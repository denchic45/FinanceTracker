package com.denchic45.financetracker.domain.usecase

import arrow.core.Ior
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.repository.CategoryRepository
import com.denchic45.financetracker.domain.model.CategoryItem
import kotlinx.coroutines.flow.Flow

class ObserveCategoryByIdUseCase(private val categoryRepository: CategoryRepository) {
    operator fun invoke(categoryId: Long): Flow<Ior<Failure, CategoryItem?>> {
        return categoryRepository.observeById(categoryId)
    }
}