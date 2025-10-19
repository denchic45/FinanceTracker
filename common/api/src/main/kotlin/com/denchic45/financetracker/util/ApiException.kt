package com.denchic45.financetracker.util

class ApiException(val code: Int, message: String) : Exception(message)