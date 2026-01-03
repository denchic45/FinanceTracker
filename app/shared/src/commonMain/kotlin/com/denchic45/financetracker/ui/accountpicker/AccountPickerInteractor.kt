package com.denchic45.financetracker.ui.accountpicker

import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.ui.PickerInteractor
import kotlinx.coroutines.channels.Channel

class AccountPickerInteractor : PickerInteractor<AccountItem>() {

    private val multipleSource = Channel<List<AccountItem>>()

    suspend fun onSelectMultiple(items: List<AccountItem>) {
        multipleSource.send(items)
    }

    suspend fun getPickedMultiple() = multipleSource.receive()
}