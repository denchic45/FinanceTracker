package com.denchic45.financetracker.error

import kotlinx.serialization.Serializable

@Serializable
object CategoryNotFound : NotFoundError() {
    override val entity: String = "category"
}