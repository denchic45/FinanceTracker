package com.denchic45.financetracker.api.user.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserResponse(
    val id: Uuid? = null,
    val firstName: String,
    val lastName: String,
    val email: String
)