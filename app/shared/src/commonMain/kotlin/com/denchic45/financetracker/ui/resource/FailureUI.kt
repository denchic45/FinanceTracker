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
import androidx.compose.ui.graphics.painter.Painter
import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.NoConnection
import com.denchic45.financetracker.data.ThrowableFailure
import com.denchic45.financetracker.data.UnknownApiFailure
import com.denchic45.financetracker.data.getDefaultErrorMessageResource
import com.denchic45.financetracker.ui.NoDataContent
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.building_broadcast_tower
import financetracker_app.shared.generated.resources.cancel
import financetracker_app.shared.generated.resources.common_error_api_unhandled
import financetracker_app.shared.generated.resources.common_error_internal
import financetracker_app.shared.generated.resources.common_error_no_connection
import financetracker_app.shared.generated.resources.common_retry
import financetracker_app.shared.generated.resources.error_circle
import financetracker_app.shared.generated.resources.lock
import financetracker_app.shared.generated.resources.mood_puzzled
import financetracker_app.shared.generated.resources.server_bolt
import io.ktor.http.HttpStatusCode
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


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
        val message = getString(
            when (error) {
                NoConnection -> Res.string.common_error_no_connection
                is ApiFailure -> Res.string.common_error_api_unhandled // TODO handle http code
                is UnknownApiFailure -> Res.string.common_error_api_unhandled
                is ThrowableFailure -> Res.string.common_error_internal // TODO send report
            }
        )
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
                                    Text(stringResource(Res.string.common_retry))
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
        iconPainter = getDefaultIcon(failure),
        title = { Text(getDefaultMessage(failure)) }
    )
}

@Composable
fun getDefaultIcon(failure: Failure): Painter = painterResource(
    when (failure) {
        is ApiFailure -> when (failure.error.httpCode) {
            HttpStatusCode.BadRequest -> Res.drawable.cancel
            HttpStatusCode.Forbidden -> Res.drawable.lock
            HttpStatusCode.NotFound -> Res.drawable.mood_puzzled
            in HttpStatusCode.InternalServerError..HttpStatusCode.InternalServerError -> Res.drawable.server_bolt
            else -> Res.drawable.error_circle
        }

        NoConnection -> Res.drawable.building_broadcast_tower
        is ThrowableFailure,
        is UnknownApiFailure -> Res.drawable.error_circle
    }
)

@Composable
fun getDefaultMessage(failure: Failure): String = stringResource(
    failure.getDefaultErrorMessageResource()
)