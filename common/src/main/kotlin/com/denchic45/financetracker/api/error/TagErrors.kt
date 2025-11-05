package com.denchic45.financetracker.api.error

import kotlinx.serialization.Serializable

@Serializable
object TagNotFound : NotFoundError() {
    override val entity: String = "tag"
}

object TagValidationMessages {
    const val NAME_REQUIRED = "Tag name is required."
}