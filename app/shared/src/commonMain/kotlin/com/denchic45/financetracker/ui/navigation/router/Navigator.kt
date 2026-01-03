package com.denchic45.financetracker.ui.navigation.router

interface Navigator<C : Any> {

     fun navigate(transformer: MutableList<C>.() -> Unit)
}
