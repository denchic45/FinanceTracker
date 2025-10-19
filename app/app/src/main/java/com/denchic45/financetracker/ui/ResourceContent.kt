package com.denchic45.financetracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.financetracker.domain.Failure
import com.denchic45.financetracker.domain.Resource

@Composable
fun <T> ResourceContent(
    resource: Resource<T>,
    onLoading: @Composable () -> Unit = { CircularLoadingBox(Modifier.fillMaxSize()) },
    onFailed: @Composable (Failure, T?) -> Unit = { failure, value -> },
    onSuccess: @Composable (T) -> Unit,
) {
    when (resource) {
        Resource.Loading -> onLoading()
        is Resource.Success -> onSuccess(resource.value)
        is Resource.Failed -> onFailed(resource.failure, resource.value)
    }
}

@Composable
fun CircularLoadingBox(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}