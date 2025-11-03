package com.denchic45.financetracker

import com.denchic45.financetracker.database.configureDatabase
import com.denchic45.financetracker.feature.account.configureAccounts
import com.denchic45.financetracker.feature.auth.configureAuth
import com.denchic45.financetracker.feature.category.configureCategory
import com.denchic45.financetracker.feature.statistics.configureStatistics
import com.denchic45.financetracker.feature.tag.configureTag
import com.denchic45.financetracker.feature.transaction.configureTransactions
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureFrameworks()
    configureDatabase()
    configureAuth()
    configureAccounts()
    configureTransactions()
    configureCategory()
    configureTag()
    configureStatistics()
}
