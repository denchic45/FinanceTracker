package com.denchic45.financetracker.ui

import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.NoConnection
import com.denchic45.financetracker.data.ThrowableFailure
import com.denchic45.financetracker.data.UnknownApiFailure
import com.denchic45.financetracker.ui.resource.UiImage
import com.denchic45.financetracker.ui.resource.UiText
import com.denchic45.financetracker.ui.resource.uiTextOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class AppEventHandler {
    private val _events = Channel<AppUIEvent>()
    val events: Flow<AppUIEvent> get() = _events.receiveAsFlow()

    private var loadingJob: Job? = null
    private val _loading = MutableStateFlow<LoadingState?>(null)
    val loading get() = _loading.asStateFlow()

    suspend fun handleFailure(failure: Failure) {
        handleFailure(failure, ::defaultApiFailure, ::defaultHandle)
    }

    fun showLongLoading(
        coroutineScope: CoroutineScope,
        state: LoadingState = LoadingState(uiTextOf("Требуется больше времени..."))
    ) {
        loadingJob?.cancel()
        loadingJob = coroutineScope.launch {
            delay(3.seconds)
            _loading.value = state
        }
    }

    fun hideLongLoading() {
        loadingJob?.cancel()
        loadingJob = null
        _loading.value = null
    }

    suspend fun handleFailure(
        failure: Failure,
        onApiFailureHandle: (ApiFailure) -> AppUIEvent,
        onHandle: (Failure) -> AppUIEvent = { defaultHandle(failure, onApiFailureHandle) }
    ) {
        _events.send(onHandle(failure))
    }

    fun defaultHandle(
        failure: Failure,
        onApiFailureHandle: (ApiFailure) -> AppUIEvent = { defaultApiFailure(it) }
    ) = when (failure) {
        NoConnection -> AppUIEvent.Toast(uiTextOf("No internet connection"))
        is UnknownApiFailure -> AppUIEvent.Toast(uiTextOf("Unknown API error"))
        is ApiFailure -> onApiFailureHandle(failure)
        is ThrowableFailure -> AppUIEvent.AlertMessage(
            title = uiTextOf("Throwable error"),
            text = uiTextOf(failure.throwable.message.orEmpty())
        )
    }

    private fun defaultApiFailure(apiFailure: ApiFailure): AppUIEvent {
        return when (apiFailure.error) {
            else -> {
                AppUIEvent.Toast(uiTextOf("Unhandled API error"))
            }
        }
    }

    suspend fun sendEvent(event: AppUIEvent) {
        _events.send(event)
    }
}

sealed interface AppUIEvent {
    data class AlertMessage(
        val title: UiText,
        val text: UiText? = null,
        val icon: UiImage? = null
    ) : AppUIEvent

    data class Toast(val message: UiText) : AppUIEvent
}

data class LoadingState(val message: UiText)