package com.denchic45.financetracker.ui.resource

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.asFailure
import kotlinx.coroutines.flow.filter


@Composable
fun <T : Any> PagingItemsContent(
    pagingItems: LazyPagingItems<T>,
    onRetry: (() -> Unit)? = null,
    retrying: Boolean = false,
    showContent: (LazyPagingItems<T>) -> Boolean = { true },
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    dataContent: @Composable (LazyPagingItems<T>) -> Unit,
    failedContent: @Composable (Failure) -> Unit = { DefaultFailedContent(it) },
    bothContent: @Composable (Failure, LazyPagingItems<T>) -> Unit = { error, data ->
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
    Column {
        val latestNonLoadingState by snapshotFlow { pagingItems.loadState.refresh }
            .filter { it !is LoadState.Loading }
            .collectAsState(LoadState.Loading)

        when (val state = latestNonLoadingState) {
            LoadState.Loading -> loadingContent()

            is LoadState.Error -> {
                if (showContent(pagingItems))
                    bothContent(state.error.asFailure(), pagingItems)
                else failedContent(state.error.asFailure())
            }

            is LoadState.NotLoading -> {
                if (showContent(pagingItems)) dataContent(pagingItems)
                else emptyDataContent()
            }
        }
    }
}