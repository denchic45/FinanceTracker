package com.denchic45.financetracker.feature.user

import com.denchic45.financetracker.api.user.model.UserResponse
import com.denchic45.financetracker.database.table.UserDao

fun UserDao.toUserResponse(): UserResponse = UserResponse(
    id = id.value,
    firstName = firstName,
    lastName = lastName,
    email = email
)