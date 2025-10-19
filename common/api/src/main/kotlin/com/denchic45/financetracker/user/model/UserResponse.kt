package com.denchic45.financetracker.user.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
)