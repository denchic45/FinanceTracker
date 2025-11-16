package com.denchic45.financetracker.ui.navigation.router

/**
 * Moves the provided [configuration] to the end of the list (the 'front' of the stack).
 * All existing instances of the same class are removed.
 */
fun <T : Any> Navigator<T>.bringToFront(configuration: T) = navigate {
    removeAll { it::class == configuration::class }
    add(configuration)
}

/**
 * Pushes the provided [configuration] at the top of the stack (the end of the list).
 */
fun <T : Any> Navigator<T>.push(configuration: T) = navigate {
    add(configuration)
}

/**
 * Pushes the provided [configuration] at the top of the stack. Does nothing if the provided
 * [configuration] is already on top of the stack.
 */
fun <T : Any> Navigator<T>.pushNew(configuration: T) = navigate {
    if (lastOrNull() != configuration) {
        add(configuration)
    }
}

/**
 * Pops the latest configuration at the top of the stack (removes the last element).
 * Does nothing if the stack size is 1 or less.
 */
fun <T : Any> Navigator<T>.pop() = navigate {
    if (size > 1) {
        removeLastOrNull()
    }
}

/**
 * Drops the configurations at the top of the stack while the [predicate] returns `true`.
 */
inline fun <T : Any> Navigator<T>.popWhile(crossinline predicate: (T) -> Boolean) = navigate {
    while (isNotEmpty() && predicate(last())) {
        removeLastOrNull()
    }
}

/**
 * Drops configurations at the top of the stack so that the provided [index] becomes active (the new top of the stack).
 *
 * @param index the index of the configuration to become active. Must not be negative.
 */
fun <T : Any> Navigator<T>.popTo(index: Int) = navigate {
    require(index >= 0) { "Index must not negative, but was $index" }

    while (size > index + 1) {
        removeLastOrNull()
    }
}

/**
 * Replaces the current configuration at the top of the stack with the provided [configuration].
 */
fun <T : Any> Navigator<T>.replaceCurrent(configuration: T) = navigate {
    if (isNotEmpty()) {
        removeLastOrNull()
    }
    add(configuration)
}

/**
 * Replaces the whole stack with the provided [configurations].
 */
fun <T : Any> Navigator<T>.replaceAll(vararg configurations: T) = navigate {
    clear()
    addAll(configurations)
}