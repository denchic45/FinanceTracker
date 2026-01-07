package com.denchic45.financetracker.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.user_circle
import org.jetbrains.compose.resources.painterResource

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
                Icon(painterResource(Res.drawable.user_circle), null)
            }
        },
        actions = actions
    )
}