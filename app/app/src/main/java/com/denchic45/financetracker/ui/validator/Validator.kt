package com.denchic45.financetracker.ui.validator


interface Validator<T> {
    fun validate(): Boolean
}

class ValueValidator<T>(
    private val value: () -> T,
    private val conditions: List<Condition<T>>,
    private val operator: Operator<Condition<T>> = Operator.allEach()
) : Validator<T> {

    override fun validate(): Boolean {
        return value().run { conditions.validate(this, operator) }
    }
}

class CompositeValidator<T>(
    private val validators: List<Validator<out T>>,
    private val operator: Operator<Validator<out T>> = Operator.allEach()
) : Validator<T> {

    override fun validate(): Boolean = validators.validate(operator)

    inline fun onValid(block: () -> Unit) {
        if (validate()) block()
    }
}

fun <T> Validator<T>.observable(result: ValidationResult): Validator<T> {
    return ObservableValidator(this, result)
}

class ObservableValidator<T>(
    private val validator: Validator<T>,
    private val onResult: ValidationResult
) : Validator<T> by validator {
    override fun validate(): Boolean {
        return validator.validate().apply { onResult(this) }
    }
}