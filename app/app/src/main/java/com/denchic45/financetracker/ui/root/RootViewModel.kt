package com.denchic45.financetracker.ui.root

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.domain.usecase.ObserveAuthStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable


class RootViewModel(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
) : ViewModel() {
    private val _backStack = mutableStateListOf<Screen>(Screen.Splash)
    val backStack: List<Screen> = _backStack

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { isAuth ->
                withContext(Dispatchers.Main) {
                    if (isAuth) {
                        _backStack.add(Screen.Main)
                    } else {
                        _backStack.add(Screen.Auth)
                    }
                }
            }
        }
    }
}

@Serializable
sealed interface Screen {
    data object Splash : Screen
    data object Auth : Screen
    data object Main : Screen
}