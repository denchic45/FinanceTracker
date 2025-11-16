package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.repository.CategoryRepository

class RemoveCategoryUseCase(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(
        categoryId: Long
    ): EmptyRequestResult {
        return categoryRepository.remove(categoryId)
    }
}