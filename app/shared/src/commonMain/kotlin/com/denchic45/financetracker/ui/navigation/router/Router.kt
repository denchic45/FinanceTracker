package com.denchic45.financetracker.ui.navigation.router

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Router<C : Any>(initial: C) : Navigator<C> {
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _backstack = mutableStateListOf(initial)
    val backstack: List<C> get() = _backstack

    override fun navigate(transformer: MutableList<C>.() -> Unit) {
        mainScope.launch { transformer(_backstack) }
    }
}
