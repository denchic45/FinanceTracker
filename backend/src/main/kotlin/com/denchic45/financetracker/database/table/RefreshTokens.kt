package com.denchic45.financetracker.database.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone


object RefreshTokens : LongIdTable("refresh_token") {
    val token = text("token")
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val expireAt = timestampWithTimeZone("expire_at")
}