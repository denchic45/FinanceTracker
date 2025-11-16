package com.denchic45.financetracker.ui.transactioneditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.Field
import com.denchic45.financetracker.FieldEditor
import com.denchic45.financetracker.api.transaction.model.AbstractTransactionRequest
import com.denchic45.financetracker.api.transaction.model.TransactionRequest
import com.denchic45.financetracker.api.transaction.model.TransactionType
import com.denchic45.financetracker.api.transaction.model.TransferTransactionRequest
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TagItem
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.domain.usecase.AddTransactionUseCase
import com.denchic45.financetracker.domain.usecase.ObserveTransactionByIdUseCase
import com.denchic45.financetracker.domain.usecase.RemoveTransactionUseCase
import com.denchic45.financetracker.domain.usecase.UpdateTransactionUseCase
import com.denchic45.financetracker.ui.AppEventHandler
import com.denchic45.financetracker.ui.accountpicker.AccountPickerInteractor
import com.denchic45.financetracker.ui.categorypicker.CategoryPickerInteractor
import com.denchic45.financetracker.ui.main.NavEntry
import com.denchic45.financetracker.ui.navigation.router.pop
import com.denchic45.financetracker.ui.navigation.router.push
import com.denchic45.financetracker.ui.tagspicker.TagsPickerInteractor
import com.denchic45.financetracker.ui.util.convertToMonetaryFormat
import com.denchic45.financetracker.ui.util.currencyRegex
import com.denchic45.financetracker.ui.validator.CompositeValidator
import com.denchic45.financetracker.ui.validator.Condition
import com.denchic45.financetracker.ui.validator.Operator
import com.denchic45.financetracker.ui.validator.ValueValidator
import com.denchic45.financetracker.ui.validator.getIfNot
import com.denchic45.financetracker.ui.validator.observable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TransactionEditorViewModel(
    val transactionId: Long?,
    private val accountPickerInteractor: AccountPickerInteractor,
    private val categoryPickerInteractor: CategoryPickerInteractor,
    private val tagsPickerInteractor: TagsPickerInteractor,
    private val observeTransactionByIdUseCase: ObserveTransactionByIdUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val removeTransactionUseCase: RemoveTransactionUseCase,
    private val appEventHandler: AppEventHandler,
    private val router: AppRouter
) : ViewModel() {
    val state = EditingTransactionState(transactionId == null)

    private val fieldEditor = FieldEditor(
        mapOf(
            "type" to Field(state::transactionType),
            "datetime" to Field(state::datetime),
            "amountText" to Field(state::amountText),
            "note" to Field(state::note),
            "sourceAccount" to Field(state::sourceAccount),
            "category" to Field(state::category),
            "tags" to Field(state::tags),
            "incomeAccount" to Field(state::incomeAccount)
        )
    )

    private val formValidator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = state::amountText,
                conditions = listOf(
                    Condition(String::isNotEmpty).observable { isValid ->
                        state.amountMessage = getIfNot(isValid) { "Сумма обязательна" }
                    },
                    Condition<String>({ (it.toFloatOrNull() ?: 0F) > 0F }).observable { isValid ->
                        if (!isValid && state.amountText.isNotEmpty()) {
                            state.amountMessage = "Введите положительное число"
                        } else if (isValid && state.amountMessage == "Введите положительное число") {
                            state.amountMessage = null
                        }
                    }
                ),
                operator = Operator.allEach()
            ),
            ValueValidator(
                value = state::sourceAccount,
                conditions = listOf(
                    Condition<AccountItem?> { it != null }.observable { isValid ->
                        state.sourceAccountMessage =
                            getIfNot(isValid) { "Выберите счет отправителя" }
                    }
                )
            ),
            ValueValidator(
                value = state::category,
                conditions = listOf(
                    Condition<CategoryItem?> { state.transactionType == TransactionType.TRANSFER || it != null }.observable { isValid ->
                        state.categoryMessage = getIfNot(isValid) { "Выберите категорию" }
                    }
                )
            ),
            ValueValidator(
                value = state::incomeAccount,
                conditions = listOf(
                    Condition<AccountItem?> { state.transactionType != TransactionType.TRANSFER || it != null }.observable { isValid ->
                        state.incomeAccountMessage =
                            getIfNot(isValid) { "Выберите счет получателя" }
                    }
                )
            )
        ),
        operator = Operator.allEach()
    )

    init {
        viewModelScope.launch {
            transactionId?.let {
                val result = observeTransactionByIdUseCase(transactionId).first()
                result.getOrNull()?.let { transaction ->
                    state.datetime = transaction.datetime
                    state.amountText = transaction.amount.convertToMonetaryFormat()
                    state.note = transaction.note
                    state.sourceAccount = transaction.account
                    when (transaction) {
                        is TransactionItem.Expense -> {
                            state.transactionType = TransactionType.EXPENSE
                            state.category = transaction.category
                            state.tags = transaction.tags
                            state.incomeAccount = null
                        }

                        is TransactionItem.Income -> {
                            state.transactionType = TransactionType.INCOME
                            state.category = transaction.category
                            state.tags = transaction.tags
                            state.incomeAccount = null
                            state.tags = emptyList()
                        }

                        is TransactionItem.Transfer -> {
                            state.transactionType = TransactionType.TRANSFER
                            state.incomeAccount = transaction.incomeAccount
                            state.category = null
                            state.tags = emptyList()
                        }
                    }
                }
            }
        }
    }

    fun onTransactionTypeChange(type: TransactionType) {
        if (type == state.transactionType) return
        state.transactionType = type
        if (type != TransactionType.TRANSFER) {
            state.category = null
            state.incomeAccount = null
        } else {
            state.category = null
            state.tags = emptyList()
        }
    }

    fun onDateChange(date: LocalDate) {
        state.datetime = date.atTime(state.datetime.time)
        state.datetimeMessage = null
    }

    fun onTimeChange(time: LocalTime) {
        state.datetime = time.atDate(state.datetime.date)
        state.datetimeMessage = null
    }

    fun onAmountTextChange(amountText: String) {
        if (amountText.matches(currencyRegex)) state.amountText = amountText
        state.amountMessage = null
    }

    fun onNoteChange(note: String) {
        state.note = note
    }

    fun onSaveClick() {
        if (!formValidator.validate()) return
        state.isLoading = true
        viewModelScope.launch {
            val result = transactionId?.let {
                updateTransactionUseCase(
                    transactionId,
                    state.toRequest()
                )
            } ?: addTransactionUseCase(state.toRequest())
            state.isLoading = false
            result.onLeft { failure ->
                appEventHandler.handleFailure(failure)
            }.onRight { router.pop() }
        }
    }

    fun EditingTransactionState.toRequest(): AbstractTransactionRequest {
        fun parseAmountToMinorUnits(amountText: String): Long {
            val cleanText = amountText.replace(",", "").trim()
            return (cleanText.toDouble() * 100).toLong()
        }

        return when (transactionType) {
            TransactionType.EXPENSE, TransactionType.INCOME -> TransactionRequest(
                income = (transactionType == TransactionType.INCOME),
                datetime = datetime,
                amount = parseAmountToMinorUnits(amountText),
                note = note,
                accountId = sourceAccount!!.id,

                categoryId = category!!.id,
                tagIds = tags.map { it.id }
            )

            TransactionType.TRANSFER -> TransferTransactionRequest(
                datetime = datetime,
                amount = parseAmountToMinorUnits(amountText),
                note = note,
                accountId = sourceAccount!!.id,
                incomeSourceId = incomeAccount!!.id
            )
        }
    }

    fun hasChanges(): Boolean {
        return fieldEditor.hasChanges()
    }

    fun onSourceAccountPickerClick() {
        router.push(NavEntry.AccountPicker(null))
        viewModelScope.launch {
            state.sourceAccount = accountPickerInteractor.getPicked()
            state.sourceAccountMessage = null
        }
    }

    fun onIncomeAccountPickerClick() {
        router.push(NavEntry.AccountPicker(null))
        viewModelScope.launch {
            state.incomeAccount = accountPickerInteractor.getPicked()
            state.incomeAccountMessage = null
        }
    }

    fun onCategoryPickerClick() {
        router.push(
            NavEntry.CategoryPicker(
                when (state.transactionType) {
                    TransactionType.INCOME -> true
                    TransactionType.EXPENSE -> false
                    TransactionType.TRANSFER -> throw IllegalStateException("It is forbidden to select a category")
                }
            )
        )
        viewModelScope.launch {
            state.category = categoryPickerInteractor.getPicked()
            state.categoryMessage = null
        }
    }

    fun onTagsPickerClick() {
        router.push(NavEntry.TagsPicker(state.tags.map(TagItem::id)))
        viewModelScope.launch {
            state.tags = tagsPickerInteractor.getPicked()
        }
    }

    fun onRemoveClick() {
        viewModelScope.launch {
            state.isLoading = true
            removeTransactionUseCase(transactionId!!)
                .onNone { router.pop() }
                .onSome { failure ->
                    appEventHandler.handleFailure(failure)
                }
            state.isLoading = false
        }
    }

    fun onDismissClick() = router.pop()

}

@Stable
class EditingTransactionState(val isNew: Boolean) {
    var transactionType: TransactionType by mutableStateOf(TransactionType.EXPENSE)

    @OptIn(ExperimentalTime::class)
    var datetime: LocalDateTime by mutableStateOf(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    )
    var amountText: String by mutableStateOf("")
    var note: String by mutableStateOf("")

    var sourceAccount: AccountItem? by mutableStateOf(null)
    var incomeAccount: AccountItem? by mutableStateOf(null)

    var category: CategoryItem? by mutableStateOf(null)
    var tags: List<TagItem> by mutableStateOf(emptyList())


    // 4. Validation/Error Messages
    var amountMessage: String? by mutableStateOf(null)
    var datetimeMessage: String? by mutableStateOf(null)
    var sourceAccountMessage: String? by mutableStateOf(null)
    var categoryMessage: String? by mutableStateOf(null)
    var incomeAccountMessage: String? by mutableStateOf(null)
    var noteMessage: String? by mutableStateOf(null)

    // 5. Generic UI State
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
}

