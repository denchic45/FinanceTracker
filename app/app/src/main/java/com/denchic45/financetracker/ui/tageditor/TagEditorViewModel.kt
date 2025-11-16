package com.denchic45.financetracker.ui.tageditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.api.tag.model.TagRequest
import com.denchic45.financetracker.data.ifHasRightValue
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.AddTagUseCase
import com.denchic45.financetracker.domain.usecase.ObserveTagByIdUseCase
import com.denchic45.financetracker.domain.usecase.UpdateTagUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
import com.denchic45.financetracker.ui.validator.getIfNot
import com.denchic45.financetracker.ui.validator.observable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class TagEditorViewModel(
    private val tagId: Long?,
    private val observeTagByIdUseCase: ObserveTagByIdUseCase,
    private val addTagUseCase: AddTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val router: AppRouter,
    private val appEventHandler: AppEventHandler
) : ViewModel() {

    val state = EditingTagState(tagId == null)


    private val formValidator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = state::name,
                conditions = listOf(
                    Condition(String::isNotEmpty)
                        .observable { isValid ->
                            state.nameMessage = getIfNot(isValid) {
                                "Название тега не может быть пустым"
                            }
                        }
                ),
                operator = Operator.allEach()
            )
        ),
        operator = Operator.allEach()
    )


    init {
        tagId?.let { id ->
            viewModelScope.launch {
                state.isLoading = true
                observeTagByIdUseCase(id)
                    .first()
                    .ifHasRightValue { item ->
                        if (item == null) {
                            router.pop(); return@ifHasRightValue
                        }

                        state.name = item.name
                    }
                state.isLoading = false
            }
        }
    }

    fun onNameChange(name: String) {
        state.name = name
        formValidator.validate()
    }

    fun onDismissClick() = router.pop()

    fun onSaveClick() {
        if (!formValidator.validate()) return

        state.isLoading = true
        viewModelScope.launch {
            val request = state.toRequest()
            val result = if (tagId == null) {
                addTagUseCase(request)
            } else {
                updateTagUseCase(tagId, request)
            }

            state.isLoading = false
            result.onLeft { failure ->
                appEventHandler.handleFailure(failure)
            }.onRight { router.pop() }
        }
    }
}

@Stable
class EditingTagState(val isNew: Boolean) {
    var name: String by mutableStateOf("")

    var nameMessage: String? by mutableStateOf(null)

    // 5. Generic UI State
    var isLoading: Boolean by mutableStateOf(false)
}

fun EditingTagState.toRequest() = TagRequest(name)