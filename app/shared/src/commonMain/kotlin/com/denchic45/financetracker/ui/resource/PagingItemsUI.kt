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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.denchic45.financetracker.data.ApiFailure
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.NoConnection
import com.denchic45.financetracker.data.ThrowableFailure
import com.denchic45.financetracker.data.UnknownApiFailure
import com.denchic45.financetracker.data.asFailure
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map


private val Failure.message: String
    get() = when (val error = this) {
        NoConnection -> "Нет подключения к интернету"
        is ApiFailure -> "Ошибка с серевера - ${error.error.httpCode.value}"
        is UnknownApiFailure -> "Неизвестная ошибка с сервера"
        is ThrowableFailure -> "Неизвестная ошибка"
    }


@Composable
fun <T : Any> PagingItemsContent(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<T>,
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    dataContent: @Composable (LazyPagingItems<T>) -> Unit,
    failedContent: @Composable (Failure) -> Unit = { DefaultFailedContent(it) },
    emptyDataContent: @Composable () -> Unit
) {
    var isRetrying by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val latestNonLoadingState by snapshotFlow { pagingItems.loadState.refresh }
        .filter { it !is LoadState.Loading }
        .map {
            isRetrying = false
            isRefreshing = false
            it as? LoadState.Error
        }.collectAsState(LoadState.Loading)

    if (latestNonLoadingState is LoadState.Loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            loadingContent()
        }
    } else SnackbarResourceLayout(
        modifier = modifier,
        snackbarHostState = remember { SnackbarHostState() },
        isRetrying = isRetrying,
        isRefreshing = isRefreshing,
        onRetry = {
            isRetrying = true
            pagingItems.retry()
        },
        onRefresh = {
            isRefreshing = true
            pagingItems.refresh()
        },
        data = pagingItems,
        failure = (latestNonLoadingState as? LoadState.Error)?.error?.asFailure(),
        showContent = { pagingItems.itemCount > 0 },
        dataContent = dataContent,
        failedContent = failedContent,
        emptyDataContent = emptyDataContent
    )
}

@Composable
fun <T> SnackbarResourceLayout(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    isRetrying: Boolean,
    isRefreshing: Boolean,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    data: T?,
    failure: Failure?,
    showContent: () -> Boolean,
    dataContent: @Composable ((T) -> Unit),
    failedContent: @Composable ((Failure) -> Unit),
    emptyDataContent: @Composable (() -> Unit)
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(
                    action = {
                        if (!isRetrying)
                            TextButton(onClick = onRetry) {
                                Text("Повторить")
                            }
                        else CircularProgressIndicator()
                    }
                ) {
                    Text(snackbarData.visuals.message)
                }
            }
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = isRefreshing,
            onRefresh = { onRefresh() },
            state = rememberPullToRefreshState()
        ) {
            Box(Modifier.fillMaxSize()) {
                when {
                    failure != null && data == null -> failedContent(failure)

                    failure == null && data != null -> if (showContent()) {
                        dataContent(data)
                    } else emptyDataContent()

                    failure != null && data != null -> if (showContent()) {
                        dataContent(data)
                        LaunchedEffect(Unit) {
                            snackbarHostState.showSnackbar(
                                message = failure.message,
                                duration = SnackbarDuration.Indefinite
                            )
                        }

                    } else failedContent(failure)

                    else -> throw IllegalStateException("Error or data must be not null.")
                }
            }
        }
    }
}