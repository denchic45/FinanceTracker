package com.denchic45.financetracker.database.util

import org.jetbrains.exposed.v1.core.FieldSet
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.exists
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

fun <T : FieldSet> T.exists(where: () -> Op<Boolean>): Boolean {
    val existsOp = exists(selectAll().where(where))
    val result = Table.Dual.select(existsOp).first()
    return result[existsOp]
}