package com.denchic45.financetracker.ui

import kotlinx.coroutines.channels.Channel

abstract class PickerInteractor<T> {
    private val source = Channel<T?>()

    suspend fun onSelect(value: T) {
        source.send(value)
    }

    suspend fun onDismiss() {
        source.send(null)
    }


    suspend fun getPicked() = source.receive()
}