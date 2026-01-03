package com.denchic45.financetracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.ui.resource.UiImage
import com.denchic45.financetracker.ui.resource.getPainter

@Composable
fun NoDataContent(
    modifier: Modifier = Modifier,
    icon: UiImage? = null,
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon?.let {
                Image(
                    modifier = Modifier.size(172.dp),
                    painter = icon.getPainter(),
                    contentDescription = null
                )
            }
            Spacer(Modifier.height(8.dp))
            ProvideTextStyle(value = MaterialTheme.typography.titleMedium) {
                title()
            }

            description?.let {
                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) { it() }
            }

            Spacer(Modifier.height(8.dp))
            action?.invoke()
        }
    }
}