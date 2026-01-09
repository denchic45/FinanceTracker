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
import com.denchic45.financetracker.data.AppPreferences
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.CurrencyHandler
import com.denchic45.financetracker.domain.model.Currency
import com.denchic45.financetracker.domain.usecase.AddAccountUseCase
import com.denchic45.financetracker.domain.usecase.ObserveAccountByIdUseCase
import com.denchic45.financetracker.domain.usecase.UpdateAccountUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
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
    private val appPreferences: AppPreferences,
    private val currencyHandler: CurrencyHandler,
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
                        state.showNameError = !isValid
                    }
                )
            ),
            ValueValidator(
                value = state::balance,
                conditions = listOf(
                    Condition<String>({ accountId != null || it.toFloatOrNull() != null })
                        .observable { isValid ->
                            state.showBalanceError = !isValid
                        }
                ),
                operator = Operator.allEach()
            )
        ),
        operator = Operator.allEach()
    )

    init {
        viewModelScope.launch {
            state.currency = appPreferences.defaultCurrency.first()

            viewModelScope.launch {
                accountId?.let { id ->
                    findAccountByIdUseCase(id).first().getOrNull()?.let { account ->
                        state.name = account.name
                        state.type = account.type
                        state.balance = currencyHandler.formatForEditing(account.balance)
                        fieldEditor.updateOldValues()
                    }
                }
            }
        }
    }

    fun onAccountNameChanged(name: String) {
        state.name = name
        state.showNameError = false
    }

    fun onAccountTypeChange(type: AccountType) {
        state.type = type
        state.iconName = when (type) {
            AccountType.ORDINARY -> "wallet"
            AccountType.DEBT -> "credit_card"
            AccountType.SAVINGS -> "pig_money"
        }
    }

    fun onBalanceChanged(balance: String) {
        currencyHandler.filterInput(balance)?.let { state.balance = it }
        state.showBalanceError = false
    }

    fun onIconPick(iconName: String) {
        state.iconName = iconName
    }

    fun onSaveClick() {
        if (!formValidator.validate()) return

        state.isLoading = true
        appEventHandler.showLongLoading(viewModelScope)
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
            appEventHandler.hideLongLoading()
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
    var type by mutableStateOf(AccountType.ORDINARY)
    var balance by mutableStateOf("")
    var iconName by mutableStateOf("wallet")
    var currency by mutableStateOf<Currency?>(null)

    var createTransactionForUpdateBalance by mutableStateOf(false)
    var showNameError by mutableStateOf(false)
    var showBalanceError by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    val realBalance: Long
        get() = (balance.toFloat() * 100).toLong()
}

private fun EditingAccountState.toCreateRequest(): CreateAccountRequest {
    return CreateAccountRequest(
        name = name,
        type = type,
        initialBalance = realBalance,
        iconName = iconName
    )
}

private fun EditingAccountState.toUpdateRequest(balanceChanged: Boolean) = UpdateAccountRequest(
    name = name,
    type = type,
    adjustBalance = if (balanceChanged) AdjustAccountBalanceRequest(
        balance = realBalance,
        createTransaction = createTransactionForUpdateBalance,

        ) else null,
    iconName = iconName
)
