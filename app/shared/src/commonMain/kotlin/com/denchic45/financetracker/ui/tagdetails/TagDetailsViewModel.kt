package com.denchic45.financetracker.ui.tagdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.data.filterNotNullValue
import com.denchic45.financetracker.data.onRightHasNull
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveTagByIdUseCase
import com.denchic45.financetracker.domain.usecase.RemoveTagUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TagDetailsViewModel(
    private val tagId: Long,
    observeTagByIdUseCase: ObserveTagByIdUseCase,
    private val removeTagUseCase: RemoveTagUseCase,
    private val router: AppRouter,
    private val appEventHandler: AppEventHandler
) : ViewModel() {
    val tag = observeTagByIdUseCase(tagId)
        .onEach {
            it.onRightHasNull { router.pop() }
        }
        .filterNotNullValue()
        .stateInCacheableResource(viewModelScope)

    fun onEditClick() {
        router.push(NavEntry.TagEditor(tagId))
    }

    fun onRemoveClick() {
        viewModelScope.launch {
            removeTagUseCase(tagId)
                .onSome { appEventHandler.handleFailure(it) }
        }
    }

    fun onDismissClick() = router.pop()
}