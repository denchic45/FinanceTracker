package com.denchic45.financetracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun NoDataContent(
    modifier: Modifier = Modifier,
    iconPainter: Painter? = null,
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            iconPainter?.let {
                Image(
                    modifier = Modifier.size(172.dp).padding(bottom = 8.dp),
                    painter = iconPainter,
                    contentDescription = null
                )
            }

            ProvideTextStyle(value = MaterialTheme.typography.titleMedium) {
                title()
            }
            description?.let {
                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) { description() }
            }
            Spacer(Modifier.height(8.dp))
            action?.let {
                action()
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}