package com.denchic45.financetracker.ui.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.api.auth.model.SignInRequest
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.domain.usecase.SignInUseCase
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
import com.denchic45.financetracker.ui.validator.isEmail
import com.denchic45.financetracker.ui.validator.observable
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {
    val uiState = SignInUiState()

    private val emailValidator = ValueValidator(
        value = uiState::email,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                uiState.showEmailError = !isValid
            },
            Condition(String::isEmail).observable { isValid ->
                uiState.showEmailError = !isValid
            }
        ),
        operator = Operator.allEach()
    )

    private val passwordValidator = ValueValidator(
        value = uiState::password,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                uiState.showPasswordError = !isValid
            },
            Condition<String> { it.length >= 6 }.observable { isValid ->
                uiState.showPasswordError = !isValid
            }
        ),
        operator = Operator.allEach()
    )

    private val formValidator = CompositeValidator(
        listOf(emailValidator, passwordValidator),
        operator = Operator.allEach()
    )

    fun validate(): Boolean {
        uiState.resultError = null
        emailValidator.validate()
        passwordValidator.validate()
        return formValidator.validate()
    }

    fun onSignInClick() {
        if (!validate()) return

        viewModelScope.launch {
            uiState.isLoading = true

            signInUseCase(SignInRequest(uiState.email, uiState.password))
                .onSome { failure ->
                    uiState.isLoading = false
                    uiState.resultError = failure
                }.onNone {
                    uiState.isLoading = false
                }
        }
    }
}

@Stable
class SignInUiState {
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var showEmailError by mutableStateOf(false)
    var showPasswordError by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var resultError by mutableStateOf<Failure?>(null)
}
