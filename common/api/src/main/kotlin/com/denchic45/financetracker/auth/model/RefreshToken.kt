package com.denchic45.financetracker.auth.model

import java.time.OffsetDateTime
import java.util.*

data class RefreshToken(
    val userId: UUID,
    val token: String,
    val expireAt: OffsetDateTime
) {
    val isExpired: Boolean
        get() = expireAt < OffsetDateTime.now()
}
