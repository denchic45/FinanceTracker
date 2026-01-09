package com.denchic45.financetracker.ui.resource

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.data.Failure

@Composable
fun <T> CacheableResourceContent(
    resource: CacheableResource<T>,
    content: @Composable (T) -> Unit
) {
    CacheableResourceContent(resource, dataContent = content)
}


@Composable
fun <T> CacheableResourceContent(
    resource: CacheableResource<T>,
    onRetry: (() -> Unit)? = null,
    retrying: Boolean = false,
    loadingContent: @Composable () -> Unit = { CircularLoadingBox(Modifier.fillMaxSize()) },
    dataContent: @Composable (T) -> Unit,
    failedContent: @Composable (Failure) -> Unit = { DefaultFailedContent(it) },
    bothContent: @Composable (Failure, T) -> Unit = { failure, data ->
        DefaultBothContent(
            error = failure,
            data = data,
            onRetry = onRetry,
            retrying = retrying,
            dataContent = dataContent
        )
    }
) {
    resource.onLoading { loadingContent() }
        .onNewest { data -> dataContent(data) }
        .onCached { failure, data -> bothContent(failure, data) }
        .onFailed { it -> failedContent(it) }
}

@Composable
fun <T> CacheableResourceListContent(
    resource: CacheableResource<List<T>>,
    onRetry: (() -> Unit)? = null,
    retrying: Boolean = false,
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
        loadingContent = loadingContent,
        dataContent = {
            if (it.isNotEmpty()) dataContent(it)
            else emptyDataContent()
        },
        failedContent = failedContent,
        bothContent = { failure, items ->
            if (items.isNotEmpty()) bothContent(failure, items)
            else failedContent(failure)
        }
    )
}