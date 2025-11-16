package com.denchic45.financetracker.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveCategoriesUseCase
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource

class CategoriesViewModel(
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val router: AppRouter
) : ViewModel() {
     val incomeCategories = observeCategoriesUseCase(true)
        .stateInCacheableResource(viewModelScope)

     val expenseCategories = observeCategoriesUseCase(false)
        .stateInCacheableResource(viewModelScope)

    fun onCategoryClick(categoryId: Long) {
        router.push(NavEntry.CategoryDetails(categoryId))
    }

    fun onCreateCategoryClick(income: Boolean) {
        router.push(NavEntry.CategoryEditor(null,income))
    }
}