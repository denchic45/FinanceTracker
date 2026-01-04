package com.denchic45.financetracker.ui.resource

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.NoConnection
import com.denchic45.financetracker.data.ThrowableFailure
import com.denchic45.financetracker.data.UnknownApiFailure
import com.denchic45.financetracker.ui.NoDataContent


@Composable
fun DefaultLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { CircularLoadingBox() }
}

@Composable
fun <T> DefaultBothContent(
    error: Failure,
    data: T,
    onRetry: (() -> Unit)?,
    retrying: Boolean,
    dataContent: @Composable (T) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        val message = when (error) {
            NoConnection -> "Нет подключения к интернету"
            is ApiFailure -> "Ошибка с серевера - ${error.error.httpCode.value}"
            is UnknownApiFailure -> "Неизвестная ошибка с сервера"
            is ThrowableFailure -> "Неизвестная ошибка"
        }
        snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Indefinite)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    action = onRetry?.let {
                        {
                            if (!retrying)
                                TextButton(onClick = onRetry) {
                                    Text("Повторить")
                                }
                            else CircularProgressIndicator()
                        }
                    }
                ) {
                    Text(it.visuals.message)
                }
            }
        }
    ) {
        Box(Modifier.padding(it)) {
            dataContent(data)
        }
    }
}

@Composable
fun DefaultFailedContent(failure: Failure) {
    LaunchedEffect(failure) {
        println("Failure: $failure")
    }

    NoDataContent(
        title = { Text("Неизвестная ошибка") }
    )
}