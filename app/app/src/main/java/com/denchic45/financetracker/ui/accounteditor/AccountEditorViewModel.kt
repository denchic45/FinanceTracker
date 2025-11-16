package com.denchic45.financetracker.ui.accounteditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.Field
import com.denchic45.financetracker.FieldEditor
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.api.account.model.AdjustAccountBalanceRequest
import com.denchic45.financetracker.api.account.model.CreateAccountRequest
import com.denchic45.financetracker.api.account.model.UpdateAccountRequest
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.usecase.AddAccountUseCase
import com.denchic45.financetracker.domain.usecase.ObserveAccountByIdUseCase
import com.denchic45.financetracker.domain.usecase.UpdateAccountUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.util.convertToAmount
import com.denchic45.financetracker.ui.util.currencyRegex
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
import com.denchic45.financetracker.ui.validator.getIfNot
import com.denchic45.financetracker.ui.validator.observable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class AccountEditorViewModel(
    private val accountId: UUID?,
    private val findAccountByIdUseCase: ObserveAccountByIdUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val appEventHandler: AppEventHandler,
    private val router: AppRouter
) : ViewModel() {

    val state = EditingAccountState()


    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field(state::name),
            "type" to Field(state::type),
            "balance" to Field(state::balance)
        )
    )

    private val formValidator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = state::name,
                conditions = listOf(
                    Condition(String::isNotEmpty).observable { isValid ->
                        state.nameMessage = getIfNot(isValid) { "Name is required" }
                    }
                )
            ),
            ValueValidator(
                value = state::balance,
                conditions = listOf(
                    Condition(String::isNotEmpty).observable { isValid ->
                        state.balanceMessage =
                            getIfNot(isValid) { "Initial balance is required" }
                    },
                    Condition<String>({ it.toDoubleOrNull() != null }).observable { isValid ->
                        if (!isValid && state.balance.isNotEmpty()) {
                            state.balanceMessage = "Must be a number"
                        }
                    }
                ),
                operator = Operator.allEach()
            )
        ),
        operator = Operator.allEach()
    )

    init {
        viewModelScope.launch {
            accountId?.let { id ->
                findAccountByIdUseCase(id).first().getOrNull()?.let { account ->
                    state.name = account.name
                    state.type = account.type
                    state.balance = account.balance.convertToAmount()
                    fieldEditor.updateOldValues()
                }
            }
        }
    }

    fun onAccountNameChanged(name: String) {
        state.name = name
        state.nameMessage = null
    }

    fun onAccountTypeChange(type: AccountType) {
        state.type = type
    }

    fun onBalanceChanged(balance: String) {
        if (balance.matches(currencyRegex)) state.balance = balance
        state.balanceMessage = null
    }

    fun onSaveClick() {
        if (!formValidator.validate()) return

        state.isLoading = true
        viewModelScope.launch {
            val result = if (accountId == null) {
                addAccountUseCase(state.toCreateRequest())
            } else {
                updateAccountUseCase(
                    accountId,
                    state.toUpdateRequest(fieldEditor.fieldChanged("balance"))
                )
            }
            state.isLoading = false
            result.onLeft { failure -> appEventHandler.handleFailure(failure) }
                .onRight { router.pop() }
        }
    }

    fun hasChanges(): Boolean {
        return fieldEditor.hasChanges()
    }

    fun onDismissClick() = router.pop()
}

@Stable
class EditingAccountState {
    var name by mutableStateOf("")
    var type by mutableStateOf(AccountType.CASH)
    var balance by mutableStateOf("")
    var createTransactionForUpdateBalance by mutableStateOf(false)
    var nameMessage: String? by mutableStateOf(null)
    var balanceMessage: String? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)

    val realBalance: Long
        get() = (balance.toFloat() * 100).toLong()
}

private fun EditingAccountState.toCreateRequest(): CreateAccountRequest {
    return CreateAccountRequest(
        name = name,
        type = type,
        initialBalance = realBalance,
    )
}

private fun EditingAccountState.toUpdateRequest(balanceChanged: Boolean) = UpdateAccountRequest(
    name = name,
    type = type,
    adjustBalance = if (balanceChanged) AdjustAccountBalanceRequest(
        balance = realBalance,
        createTransaction = createTransactionForUpdateBalance
    ) else null,
)
