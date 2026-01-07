package com.denchic45.financetracker.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    navigateBack: () -> Unit
) {
    TopAppBar(
        navigationIcon = { NavigationBackIcon(navigateBack) },
        title = title,
        actions = actions
    )
}

@Composable
fun NavigationBackIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
    }
}