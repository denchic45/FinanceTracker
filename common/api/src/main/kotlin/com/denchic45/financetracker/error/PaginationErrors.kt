package com.denchic45.financetracker.error

import kotlinx.serialization.Serializable

@Serializable
class InvalidPageSize(val maxPageSize: Int, val actualPageSize: Int) : BadRequestError() {
    override val message: String = """
        Provided page size is out of the range. Page size can range from 0 to ${maxPageSize}.
        Actual is $actualPageSize
    """.trimIndent()
}