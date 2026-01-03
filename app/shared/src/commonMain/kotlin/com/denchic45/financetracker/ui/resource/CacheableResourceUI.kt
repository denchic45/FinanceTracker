package com.denchic45.financetracker.ui.resource

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.data.Failure


@Composable
fun <T> CacheableResourceContent(
    resource: CacheableResource<T>,
    onRetry: (() -> Unit)? = null,
    retrying: Boolean = false,
    showContent: (T) -> Boolean = { true },
    loadingContent: @Composable () -> Unit = { CircularLoadingBox(Modifier.fillMaxSize()) },
    dataContent: @Composable (T) -> Unit,
    failedContent: @Composable (Failure) -> Unit = { DefaultFailedContent(it) },
    bothContent: @Composable (Failure, T) -> Unit = { error, data ->
        DefaultBothContent(
            error = error,
            data = data,
            onRetry = onRetry,
            retrying = retrying,
            dataContent = dataContent
        )
    },
    emptyDataContent: @Composable () -> Unit
) {
    resource.onLoading { loadingContent() }
        .onNewest { data ->
            if (showContent(data)) dataContent(data)
            else emptyDataContent()
        }.onCached { error, data ->
            if (showContent(data)) bothContent(error, data)
            else failedContent(error)
        }.onFailed { failedContent(it) }
}

@Composable
fun <T> CacheableResourceListContent(
    resource: CacheableResource<List<T>>,
    onRetry: (() -> Unit)? = null,
    retrying: Boolean = false,
    showContent: (List<T>) -> Boolean = List<T>::isNotEmpty,
    loadingContent: @Composable () -> Unit = { CircularLoadingBox(Modifier.fillMaxSize()) },
    dataContent: @Composable (List<T>) -> Unit,
    failedContent: @Composable (Failure) -> Unit = { DefaultFailedContent(it) },
    bothContent: @Composable (Failure, List<T>) -> Unit = { error, data ->
        DefaultBothContent(
            error = error,
            data = data,
            onRetry = onRetry,
            retrying = retrying,
            dataContent = dataContent
        )
    },
    emptyDataContent: @Composable () -> Unit = {}
) {
    CacheableResourceContent(
        resource = resource,
        onRetry = onRetry,
        retrying = retrying,
        showContent = showContent,
        loadingContent = loadingContent,
        dataContent = dataContent,
        failedContent = failedContent,
        bothContent = bothContent,
        emptyDataContent = emptyDataContent
    )
}