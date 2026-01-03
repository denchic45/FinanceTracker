package com.denchic45.financetracker.ui.validator
inline fun <R> getIf(b: Boolean?, block: () -> R): R? {
    return if (b == true) block() else null
}

inline fun <R> getIfNot(b: Boolean?, block: () -> R): R? {
    return if (b == false) block() else null
}

inline fun <T> Iterable<T>.anyEach(predicate: (T) -> Boolean): Boolean {
    var found = false
    for (element in this) if (predicate(element)) found = true
    return found
}

inline fun <T> Iterable<T>.allEach(predicate: (T) -> Boolean): Boolean {
    var notFound = true
    for (element in this) if (!predicate(element)) notFound = false
    return notFound
}