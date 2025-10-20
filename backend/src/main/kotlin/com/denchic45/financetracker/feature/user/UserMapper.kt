package com.denchic45.financetracker.feature.user

import com.denchic45.financetracker.database.table.UserDao
import com.denchic45.financetracker.user.model.UserResponse

fun UserDao.toUserResponse(): UserResponse = UserResponse(
    id = id.value,
    firstName = firstName,
    lastName = lastName,
    email = email
)