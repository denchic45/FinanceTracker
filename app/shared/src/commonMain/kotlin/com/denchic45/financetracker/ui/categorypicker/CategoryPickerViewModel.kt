package com.denchic45.financetracker.ui.categorypicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.usecase.ObserveCategoriesUseCase
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class CategoryPickerViewModel(
    val income: Boolean,
    private val categoryPickerInteractor: CategoryPickerInteractor,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val router: AppRouter
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val categories = observeCategoriesUseCase(income)
        .stateInCacheableResource(viewModelScope)

    fun onCreateCategoryClick() = router.push(NavEntry.CategoryEditor(null, income))

    fun onSelect(category: CategoryItem) {
        router.pop()
        viewModelScope.launch { categoryPickerInteractor.onSelect(category) }
    }

    fun onDismissClick() {
        router.pop()
        viewModelScope.launch { categoryPickerInteractor.onDismiss() }
    }
}