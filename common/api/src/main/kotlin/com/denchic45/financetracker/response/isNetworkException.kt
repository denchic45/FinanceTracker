package com.denchic45.financetracker.response

import java.io.IOException

fun Throwable.isNetworkException(): Boolean {
    return this is IOException
}