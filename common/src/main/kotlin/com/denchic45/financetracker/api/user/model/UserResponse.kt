package com.denchic45.financetracker.api.user.model

import com.denchic45.financetracker.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String
)