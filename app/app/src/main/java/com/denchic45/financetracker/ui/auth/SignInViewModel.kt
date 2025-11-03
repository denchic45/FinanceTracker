package com.denchic45.financetracker.ui.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.NoConnection
import com.denchic45.financetracker.domain.usecase.SignInUseCase
import com.denchic45.financetracker.error.WrongEmail
import com.denchic45.financetracker.error.WrongPassword
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
import com.denchic45.financetracker.ui.validator.getIfNot
import com.denchic45.financetracker.ui.validator.isEmail
import com.denchic45.financetracker.ui.validator.observable
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {
    val uiState = SignInUiState()

    // Email Validator is kept as a property because its conditions update a separate message state
    private val emailValidator = ValueValidator(
        value = uiState::email,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                uiState.emailMessage = getIfNot(isValid) { "Почта обязательна" }
            },
            Condition(String::isEmail).observable { isValid ->
                uiState.emailMessage = getIfNot(isValid) { "Некорректная почта" }
            }
        ),
        operator = Operator.allEach()
    )

    // Password Validator is kept as a property for the same reason
    private val passwordValidator = ValueValidator(
        value = uiState::password,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                uiState.passwordMessage = getIfNot(isValid) { "Пароль обязателен" }
            },
            Condition<String> { it.length >= 6 }.observable { isValid ->
                if (!isValid && uiState.password.isNotEmpty()) {
                    uiState.passwordMessage = "Пароль должен быть не менее 6 символов"
                } else if (isValid) {
                    uiState.passwordMessage = null
                }
            }
        ),
        operator = Operator.allEach()
    )

    private val formValidator = CompositeValidator(
        listOf(emailValidator, passwordValidator),
        operator = Operator.allEach()
    )

    fun validate(): Boolean {
        // Run all validations to update UI messages
        uiState.errorMessage = null
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
                    uiState.errorMessage = when (failure) {
                        NoConnection -> "Нет интернет-соединения"
                        is ApiFailure -> {
                            when (failure.error) {
                                is WrongEmail -> "Аккаунта с такой почтой не существует"
                                is WrongPassword -> "Неверный пароль"
                                else -> "Произошла неизвестная ошибка"
                            }
                        }

                        else -> "Произошла неизвестная ошибка"
                    }
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
    var emailMessage: String? by mutableStateOf(null)
    var passwordMessage: String? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
}