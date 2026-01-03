package com.denchic45.financetracker.ui.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.ObserveTagsUseCase
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.stateInCacheableResource

class TagsViewModel(
    observeTagsUseCase: ObserveTagsUseCase,
    private val router: AppRouter
) : ViewModel() {
    val tags = observeTagsUseCase().stateInCacheableResource(viewModelScope)

    fun onTagClick(tagId: Long) {
        router.push(NavEntry.TagDetails(tagId))
    }

    fun onCreateTagClick() {
        router.push(NavEntry.TagEditor(null))
    }

    fun onDismissClick() = router.pop()
}