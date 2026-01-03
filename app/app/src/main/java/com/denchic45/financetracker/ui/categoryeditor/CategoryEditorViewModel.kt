package com.denchic45.financetracker.ui.categoryeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.api.category.model.CreateCategoryRequest
import com.denchic45.financetracker.api.category.model.UpdateCategoryRequest
import com.denchic45.financetracker.data.onRightHasNull
import com.denchic45.financetracker.data.onRightHasValue
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.AddCategoryUseCase
import com.denchic45.financetracker.domain.usecase.ObserveCategoryByIdUseCase
import com.denchic45.financetracker.domain.usecase.UpdateCategoryUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.AppUIEvent
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.resource.uiTextOf
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
import com.denchic45.financetracker.ui.validator.getIfNot
import com.denchic45.financetracker.ui.validator.observable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class CategoryEditorViewModel(
    private val categoryId: Long?,
    private val income: Boolean?,
    private val appEventHandler: AppEventHandler,
    private val observeCategoryByIdUseCase: ObserveCategoryByIdUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val router: AppRouter
) : ViewModel() {
    val state = EditingCategoryState(categoryId)
    private val formValidator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = state::name,
                conditions = listOf(
                    Condition(String::isNotEmpty).observable { isValid ->
                        state.nameMessage = getIfNot(isValid) { "Name is required" }
                    }
                )
            )
        ),
        operator = Operator.allEach()
    )

    init {
        categoryId?.let { id ->
            observeCategoryByIdUseCase(id)
                .take(1)
                .onEach {
                    it.onRightHasNull {
                        appEventHandler.sendEvent(AppUIEvent.AlertMessage(uiTextOf("Category not found")))
                        router.pop()
                    }.onRightHasValue { category ->
                        state.name = category.name
                        state.iconName = category.iconName
                    }
                }.launchIn(viewModelScope)
        } ?: run { state.income = income ?: false }
    }

    fun onNameChange(name: String) {
        state.name = name
        state.nameMessage = null
    }

    fun onIconChange(iconName: String) {
        state.iconName = iconName
    }

    fun onIncomeChange(income: Boolean) {
        state.income = income
    }

    fun onSaveClick() {
        if (!formValidator.validate()) return

        state.isLoading = true
        appEventHandler.showLongLoading(viewModelScope)
        viewModelScope.launch {
            val result = if (categoryId == null) {
                addCategoryUseCase(state.toCreateRequest())
            } else {
                updateCategoryUseCase(categoryId, state.toUpdateRequest())
            }
            state.isLoading = false
            appEventHandler.hideLongLoading()
            result.onLeft { failure -> appEventHandler.handleFailure(failure) }
                .onRight { router.pop() }
        }
    }

    fun onDismissClick() = router.pop()
}

@Stable
class EditingCategoryState(val categoryId: Long?) {
    var name: String by mutableStateOf("")
    var iconName: String by mutableStateOf("default_category_icon")
    var income: Boolean by mutableStateOf(false)

    // Validation messages
    var nameMessage: String? by mutableStateOf(null)

    // Generic UI state
    var isLoading: Boolean by mutableStateOf(false)
}

fun EditingCategoryState.toCreateRequest() = CreateCategoryRequest(
    name = name,
    icon = iconName,
    income = income
)

fun EditingCategoryState.toUpdateRequest() = UpdateCategoryRequest(
    name = name,
    icon = iconName
)