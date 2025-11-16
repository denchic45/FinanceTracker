package com.denchic45.financetracker.domain.usecase

import arrow.core.Ior
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.repository.CategoryRepository
import com.denchic45.financetracker.domain.model.CategoryItem
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesUseCase(private val categoryRepository: CategoryRepository) {

    operator fun invoke(
        income: Boolean
    ): Flow<Ior<Failure, List<CategoryItem>>> {
        return categoryRepository.observe(income)
    }
}