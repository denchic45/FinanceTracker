package com.denchic45.financetracker.api

import kotlinx.serialization.Serializable

@Serializable
class PagingResponse<T>(
    val results: List<T>,
    val page: Int,
    val count: Int,
    val totalPages: Int
)