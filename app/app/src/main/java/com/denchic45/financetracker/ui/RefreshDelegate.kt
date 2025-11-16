package com.denchic45.financetracker.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

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

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> RefreshDelegate.refreshableFlow(
    block: suspend () -> Flow<T>
) = updateTrigger.onStart { emit(Unit) }
    .flatMapLatest { block() }
    .onEach {
        stopRefresh()
    }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> refreshableFlow(
    delegate1: RefreshDelegate,
    delegate2: RefreshDelegate,
    block: suspend () -> Flow<T>
) = merge(delegate1.updateTrigger, delegate2.updateTrigger)
    .onStart { emit(Unit) }
    .flatMapLatest { block() }
    .onEach {
        delegate1.stopRefresh()
        delegate2.stopRefresh()
    }