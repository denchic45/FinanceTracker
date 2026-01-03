package com.denchic45.financetracker.ui.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.financetracker.domain.usecase.ObserveAuthStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable


class SplashViewModel(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
) : ViewModel() {

    var screen: Screen by mutableStateOf(Screen.Splash)

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { isAuth ->
                withContext(Dispatchers.Main) {
                    screen = if (isAuth) Screen.Main else Screen.Auth
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