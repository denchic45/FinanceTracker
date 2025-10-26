package com.denchic45.financetracker.ui.auth

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlin.collections.removeLastOrNull


class AuthViewModel() : ViewModel() {
    private val _backStack = mutableStateListOf<AuthScreen>(AuthScreen.Welcome)
    val backStack: List<AuthScreen> = _backStack

    fun onNavigateBack() {
        _backStack.removeLastOrNull()
    }

    fun onSignInNavigate() {
        _backStack.add(AuthScreen.SignIn)
    }

    fun onSignUpNavigate() {
        _backStack.add(AuthScreen.SignUp)
    }
}

@Serializable
sealed interface AuthScreen {
    data object Welcome : AuthScreen
    data object SignUp : AuthScreen
    data object SignIn : AuthScreen
}