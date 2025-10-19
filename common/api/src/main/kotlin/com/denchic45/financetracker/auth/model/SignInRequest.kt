package com.denchic45.financetracker.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(val email: String, val password: String)