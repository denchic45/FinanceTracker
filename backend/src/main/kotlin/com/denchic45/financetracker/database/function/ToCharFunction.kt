package com.denchic45.financetracker.database.function

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.TextColumnType


fun <T> Expression<T>.toChar(format: String): ToChar = ToChar(this, format)

class ToChar(
    private val expression: Expression<*>,
    private val format: String
) : Function<String>(TextColumnType()) {


    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        toChar(expression, format, queryBuilder)
    }
}

fun <T> toChar(expr: Expression<T>, format: String, queryBuilder: QueryBuilder): Unit = queryBuilder {
    append("TO_CHAR(")
    append(expr)
    append(", '$format'")
    append(")")
}