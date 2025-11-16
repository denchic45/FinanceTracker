package com.denchic45.financetracker.ui.tagspicker

import androidx.compose.runtime.mutableStateSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.model.TagItem
import com.denchic45.financetracker.domain.usecase.ObserveTagsUseCase
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.resource.hasResult
import com.denchic45.financetracker.ui.resource.onData
import com.denchic45.financetracker.ui.resource.stateInCacheableResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TagsPickerViewModel(
    selectedTagIds: List<Long>,
    observeTagsUseCase: ObserveTagsUseCase,
    private val tagsPickerInteractor: TagsPickerInteractor,
    private val router: AppRouter
) : ViewModel() {
    val selectedTags = mutableStateSetOf<TagItem>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tags = observeTagsUseCase().stateInCacheableResource(viewModelScope)

    init {
        viewModelScope.launch {
            tags.first { it.hasResult() }.onData { _, items ->
                selectedTags.addAll(items.filter { it.id in selectedTagIds })
            }
        }
    }

    fun onTagSelect(tag: TagItem) {
        if (tag in selectedTags) selectedTags.remove(tag)
        else selectedTags.add(tag)
    }

    fun onCreateTagClick() {
        router.push(NavEntry.TagEditor(null))
    }

    fun onDoneClick() {
        viewModelScope.launch { tagsPickerInteractor.onSelect(selectedTags.toList()) }
        router.pop()
    }

    fun onDismissClick() = router.pop()

}