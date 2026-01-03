package com.denchic45.financetracker.ui.validator

interface Condition<T> {
    fun validate(value: T): Boolean

    companion object {
        operator fun <T> invoke(predicate: (value: T) -> Boolean): Condition<T> {
            return DefaultCondition(predicate)
        }
    }
}

class DefaultCondition<T>(private val predicate: (value: T) -> Boolean) : Condition<T> {
    override fun validate(value: T): Boolean = predicate(value)
}

fun <T> Condition<T>.observable(result: ValidationResult): Condition<T> {
    return ObservableCondition(this, result)
}

class ObservableCondition<T>(
    private val condition: Condition<T>,
    private val onResult: ValidationResult
) : Condition<T> by condition {
    override fun validate(value: T): Boolean {
        return condition.validate(value).apply { onResult(this) }
    }
}