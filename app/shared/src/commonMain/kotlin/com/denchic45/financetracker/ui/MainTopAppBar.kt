package com.denchic45.financetracker.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    onNavigationIconClick: () -> Unit,
    actions: @Composable (RowScope.() -> Unit),
) {
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(Icons.Outlined.AccountCircle, null)
            }
        },
        actions = actions
    )
}