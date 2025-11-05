package com.denchic45.financetracker.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)