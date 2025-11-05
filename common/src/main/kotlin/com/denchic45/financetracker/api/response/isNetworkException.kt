package com.denchic45.financetracker.api.response

import java.io.IOException

fun Throwable.isNetworkException(): Boolean {
    return this is IOException
}