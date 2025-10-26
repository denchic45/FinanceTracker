package com.denchic45.financetracker.error

import kotlinx.serialization.Serializable

@Serializable
object CategoryNotFound : NotFoundError() {
    override val entity: String = "category"
}

object CategoryValidationMessages {
    const val NAME_REQUIRED = "Category name is required."
    const val ICON_REQUIRED = "Category icon is required."
}