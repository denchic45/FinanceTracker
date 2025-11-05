package com.denchic45.financetracker.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String
)