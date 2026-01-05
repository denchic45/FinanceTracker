package com.denchic45.financetracker.ui.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.api.auth.model.SignUpRequest
import com.denchic45.financetracker.data.Failure
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
                uiState.emailErrorType = SignUpUiState.EmailErrorType.REQUIRED
            },
            Condition(String::isEmail).observable { isValid ->
                uiState.emailErrorType = SignUpUiState.EmailErrorType.INVALID
            }
        ),
        operator = Operator.allEach()
    )

    private val passwordValidator = ValueValidator(
        value = uiState::password,
        conditions = listOf(
            Condition(String::isNotEmpty).observable { isValid ->
                uiState.passwordErrorType = getIfNot(isValid) {
                    SignUpUiState.PasswordErrorType.REQUIRED
                }
            },
            Condition<String> { it.length >= 8 }.observable { isValid ->
                uiState.passwordErrorType = getIfNot(isValid) {
                    SignUpUiState.PasswordErrorType.TOO_SHORT
                }
            },
            Condition<String> { it.any(Char::isUpperCase) }.observable { isValid ->
                uiState.passwordErrorType = getIfNot(isValid) {
                    SignUpUiState.PasswordErrorType.TOO_SHORT
                }
            },
            Condition<String> { it.any(Char::isDigit) }.observable { isValid ->
                uiState.passwordErrorType = getIfNot(isValid) {
                    SignUpUiState.PasswordErrorType.MUST_CONTAIN_DIGITS
                }
            },
            Condition<String> { it.any(Char::isLetterOrDigit) }.observable { isValid ->
                uiState.passwordErrorType = getIfNot(isValid) {
                    SignUpUiState.PasswordErrorType.MUST_CONTAIN_DIGITS
                }
            },
        ),
        operator = Operator.all()
    )

    private val formValidator = CompositeValidator(
        listOf(
            ValueValidator(
                value = uiState::firstName,
                conditions = listOf(
                    Condition(String::isNotEmpty)
                        .observable { isValid ->
                            uiState.showFirstNameError = !isValid
                        }
                )
            ),
            ValueValidator(
                value = uiState::lastName,
                conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                    uiState.showLastNameError = !isValid
                })
            ),
            emailValidator,
            passwordValidator,
            CompositeValidator(
                listOf(
                    ValueValidator(
                        value = uiState::retryPassword,
                        conditions = listOf(Condition(String::isNotEmpty).observable { isValid ->
                            uiState.showRetryPasswordError = !isValid
                        })
                    ),
                    ValueValidator(
                        value = uiState::retryPassword,
                        conditions = listOf(Condition<String> { it == uiState.password }.observable { isValid ->
                            uiState.showRetryPasswordError = !isValid
                        })
                    )
                ),
                operator = Operator.allEach()
            )
        ),
        operator = Operator.allEach()
    )

    fun validate(): Boolean {
        uiState.resultError = null
        return formValidator.validate()
    }

    fun onSignUpClick() {
        if (!validate()) return

        viewModelScope.launch {
            uiState.isLoading = true
            uiState.resultError = null

            val request = SignUpRequest(
                firstName = uiState.firstName,
                lastName = uiState.lastName,
                email = uiState.email,
                password = uiState.password
            )

            signUpUseCase(request)
                .onSome { failure ->
                    uiState.isLoading = false
                    uiState.resultError = failure

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

    var showFirstNameError: Boolean by mutableStateOf(false)
    var showLastNameError: Boolean by mutableStateOf(false)
    var emailErrorType: EmailErrorType? by mutableStateOf(null)
    var passwordErrorType: PasswordErrorType? by mutableStateOf(null)
    var showRetryPasswordError: Boolean by mutableStateOf(false)

    // UI state for async call
    var isLoading by mutableStateOf(false)
    var resultError by mutableStateOf<Failure?>(null)

    enum class EmailErrorType { REQUIRED, INVALID }
    enum class PasswordErrorType { REQUIRED, MUST_CONTAIN_SPECIAL_CHARACTERS, MUST_CONTAIN_DIGITS, MUST_CONTAIN_UPPERCASE_LETTER, TOO_SHORT }
}
