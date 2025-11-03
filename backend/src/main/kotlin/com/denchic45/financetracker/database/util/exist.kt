package com.denchic45.financetracker.database.util

import org.jetbrains.exposed.sql.*

fun <T : FieldSet> T.exists(where: SqlExpressionBuilder.() -> Op<Boolean>): Boolean {
    val existsOp = exists(selectAll().where(where))
    val result = Table.Dual.select(existsOp).first()
    return result[existsOp]
}