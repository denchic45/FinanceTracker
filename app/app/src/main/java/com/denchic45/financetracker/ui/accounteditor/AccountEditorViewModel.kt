package com.denchic45.financetracker.ui.accounteditor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.denchic45.financetracker.Field
import com.denchic45.financetracker.FieldEditor
import com.denchic45.financetracker.account.model.AccountType

class AccountEditorViewModel(val accountId: Long?) : ViewModel() {
    val state = EditableAccountState()
    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field(state::name),
            "type" to Field(state::type)
        )
    )

    fun onAccountNameChanged(name: String) {
        state.name = name
    }

    fun onAccountTypeChange(type: AccountType) {
        state.type = type
    }

    fun onSaveClick() {

    }

    fun hasChanges(): Boolean {
        return fieldEditor.hasChanges()
    }
}

class EditableAccountState {
    var name by mutableStateOf("")
    var type by mutableStateOf(AccountType.CASH)
}