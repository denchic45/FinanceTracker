package com.denchic45.financetracker.ui.categorydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.data.filterNotNullValue
import com.denchic45.financetracker.data.onRightHasNull
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveCategoryByIdUseCase
import com.denchic45.financetracker.domain.usecase.RemoveCategoryUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CategoryDetailsViewModel(
    private val categoryId: Long,
    observeCategoryByIdUseCase: ObserveCategoryByIdUseCase,
    private val removeCategoryUseCase: RemoveCategoryUseCase,
    private val router: AppRouter,
    private val appEventHandler: AppEventHandler
) : ViewModel() {

    val category = observeCategoryByIdUseCase(categoryId)
        .onEach {
            it.onRightHasNull {
                router.pop()
            }
        }
        .filterNotNullValue()
        .stateInCacheableResource(viewModelScope)

    fun onEditClick() {
        router.push(NavEntry.CategoryEditor(categoryId,null))
    }

    fun onRemoveClick() {
        viewModelScope.launch {
            removeCategoryUseCase(categoryId)
                .onSome { appEventHandler.handleFailure(it) }
                .onNone { router.pop() }
        }
    }

    fun onDismissClick() = router.pop()

}
