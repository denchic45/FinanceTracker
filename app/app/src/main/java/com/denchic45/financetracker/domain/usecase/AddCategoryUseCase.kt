package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.api.category.model.CategoryResponse
import com.denchic45.financetracker.api.category.model.CreateCategoryRequest
import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.repository.CategoryRepository
import java.util.UUID

class AddCategoryUseCase(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(request: CreateCategoryRequest): RequestResult<CategoryResponse> {
        return categoryRepository.add(request)
    }
}