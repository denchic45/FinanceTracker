package com.denchic45.financetracker.ui

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow

class RefreshDelegate {
    private val _updateTrigger = Channel<Unit>(Channel.CONFLATED)
    val updateTrigger: Flow<Unit> = _updateTrigger.consumeAsFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    suspend fun refresh() {
        _updateTrigger.send(Unit)
        _isRefreshing.value = true
    }

    fun stopRefresh() {
        _isRefreshing.value = false
    }
}