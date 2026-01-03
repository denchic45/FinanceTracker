package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.api.category.model.CategoryResponse
import com.denchic45.financetracker.api.category.model.UpdateCategoryRequest
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.repository.CategoryRepository

class UpdateCategoryUseCase(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(
        categoryId: Long,
        request: UpdateCategoryRequest
    ): RequestResult<CategoryResponse> {
        return categoryRepository.update(categoryId, request)
    }
}