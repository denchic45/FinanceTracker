package com.denchic45.financetracker.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeaderItem(
    name: String,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(start = 16.dp, end = 4.dp, top = 0.dp, bottom = 0.dp),
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f))
        action?.invoke()
    }
}