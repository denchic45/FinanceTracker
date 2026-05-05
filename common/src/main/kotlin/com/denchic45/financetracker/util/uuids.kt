package com.denchic45.financetracker.util


import kotlin.uuid.Uuid


fun String.toUuid(): Uuid = Uuid.parse(this)