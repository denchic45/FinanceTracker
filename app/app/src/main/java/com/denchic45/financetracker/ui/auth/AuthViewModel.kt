package com.denchic45.financetracker.ui.auth

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlin.collections.removeLastOrNull


class AuthViewModel() : ViewModel() {
    private val _backStack = mutableStateListOf<Screen>(Screen.Welcome)
    val backStack: List<Screen> = _backStack

    fun onNavigateBack() {
        _backStack.removeLastOrNull()
    }

    fun onSignInNavigate() {
        _backStack.add(Screen.SignIn)
    }

    fun onSignUpNavigate() {
        _backStack.add(Screen.SignUp)
    }
}

@Serializable
sealed interface Screen {
    data object Welcome : Screen
    data object SignUp : Screen
    data object SignIn : Screen
}