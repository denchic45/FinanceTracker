package com.denchic45.financetracker.api.auth.model

import java.time.OffsetDateTime
import kotlin.uuid.Uuid

data class RefreshToken(
    val userId: Uuid,
    val token: String,
    val expireAt: OffsetDateTime
) {
    val isExpired: Boolean
        get() = expireAt < OffsetDateTime.now()
}
