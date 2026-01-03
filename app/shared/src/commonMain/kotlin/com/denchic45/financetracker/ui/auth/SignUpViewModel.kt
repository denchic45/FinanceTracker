package com.denchic45.financetracker.ui.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.api.auth.model.SignUpRequest
import com.denchic45.financetracker.api.error.EmailAlreadyUsed
import com.denchic45.financetracker.api.error.InvalidRequest
import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.NoConnection
import com.denchic45.financetracker.domain.usecase.SignUpUseCase
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
import com.denchic45.financetracker.ui.validator.getIfNot
import com.denchic45.financetracker.ui.validator.isEmail
import com.denchic45.financetracker.ui.validator.observable
import kotlinx.coroutines.launch


class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {
    val uiState = SignUpUiState()

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
        listOf(
            ValueValidator(
                value = uiState::firstName,
                conditions = listOf(
                    Condition(String::isNotEmpty)
                        .observable { isValid ->
                            uiState.firstNameMessage = getIfNot(isValid) { "Имя обязательно" }
                        }
                )
            ),
            ValueValidator(
                value = uiState::lastName,
                conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                    uiState.lastNameMessage = getIfNot(isValid) { "Фамилия обязательна" }
                })
            ),
            emailValidator,
            passwordValidator,
            CompositeValidator(
                listOf(
                    ValueValidator(
                        value = uiState::retryPassword,
                        conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                            uiState.retryPasswordMessage =
                                getIfNot(isValid) { "Повтор пароля обязателен" }
                        })
                    ),
                    ValueValidator(
                        value = uiState::retryPassword,
                        conditions = listOf(Condition<String> { it == uiState.password }.observable { isValid ->
                            if (!isValid && uiState.retryPassword.isNotEmpty() && uiState.password.isNotEmpty()) {
                                uiState.retryPasswordMessage = "Пароли не совпадают"
                            } else if (isValid) {
                                uiState.retryPasswordMessage = null
                            }
                        })
                    )
                ),
                operator = Operator.allEach()
            )
        ),
        operator = Operator.allEach()
    )

    fun validate(): Boolean {
        uiState.errorMessage = null
        return formValidator.validate()
    }

    fun onSignUpClick() {
        if (!validate()) return

        viewModelScope.launch {
            uiState.isLoading = true
            uiState.errorMessage = null

            val request = SignUpRequest(
                firstName = uiState.firstName,
                lastName = uiState.lastName,
                email = uiState.email,
                password = uiState.password
            )

            signUpUseCase(request)
                .onSome { failure ->
                    uiState.isLoading = false
                    uiState.errorMessage = when (failure) {
                        NoConnection -> "Нет интернет-соединения"
                        is ApiFailure -> {
                            when (failure.error) {
                                EmailAlreadyUsed -> "Почта уже используется"
                                is InvalidRequest -> "Ошибка в веденных данных"
                                else -> "Неизвестная ошибка"
                            }
                        }

                        else -> "Неизвестная ошибка"
                    }

                }.onNone { uiState.isLoading = false }
        }
    }
}

@Stable
class SignUpUiState {
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var retryPassword by mutableStateOf("")

    var firstNameMessage: String? by mutableStateOf(null)
    var lastNameMessage: String? by mutableStateOf(null)
    var emailMessage: String? by mutableStateOf(null)
    var passwordMessage: String? by mutableStateOf(null)
    var retryPasswordMessage: String? by mutableStateOf(null)

    // UI state for async call
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
}