package com.denchic45.financetracker.database

import com.denchic45.financetracker.database.table.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.ext.inject

fun Application.configureDatabase() {
    val environmentFlag = environment.config.property("ktor.environment").getString()
    val databaseFactory by inject<DatabaseFactory>(
        qualifier = named(
            when (environmentFlag) {
                "dev" -> "default"
                "test" -> "test"
                else -> throw IllegalStateException("Unknown environment declared in application configuration.")
            }
        ),
        parameters = { parametersOf(environment.config) }
    )
    databaseFactory.connect()
    println("connect to database...")
    databaseFactory.connect()

    transaction {
        SchemaUtils.create(Users, RefreshTokens, Accounts, Transactions, Categories, Tags, TransactionTags)
    }
    println("connected to database")
}

val databaseModule = module {
    single<DatabaseFactory>(named("default")) { DatabaseFactoryImpl(get()) }
    single<DatabaseFactory>(named("test")) { DatabaseFactoryTest() }
}