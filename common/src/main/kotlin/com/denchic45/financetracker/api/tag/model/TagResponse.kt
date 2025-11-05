package com.denchic45.financetracker.api.tag.model

import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    val id: Long,
    val name: String
)