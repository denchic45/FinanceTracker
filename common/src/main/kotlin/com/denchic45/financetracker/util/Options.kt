package com.denchic45.financetracker.util

import arrow.core.Option
import arrow.core.Some


val <A> Option<A>.someValue
    get() = (this as Some).value