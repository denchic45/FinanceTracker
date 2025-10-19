package com.denchic45.financetracker.database

import com.denchic45.financetracker.database.table.Accounts
import com.denchic45.financetracker.database.table.Categories
import com.denchic45.financetracker.database.table.Transactions
import com.denchic45.financetracker.database.table.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    println("connect to database...")

    val config = environment.config
    val databaseUrl = config.property("database.url").getString()
    val databaseUser = config.property("database.user").getString()
    val databasePassword = config.property("database.password").getString()

    println("connected to database")

    transaction(
        Database.connect(
            url = databaseUrl,
            user = databaseUser,
            password = databasePassword
        )
    ) {
        if (!Users.exists() || Users.selectAll().count() == 0L) {
            SchemaUtils.create(Users, Accounts, Transactions, Categories)
            insertInitData()
        }
    }
    println("connection successful")
}

/**
 * Init data if database does not exist
 */
private fun insertInitData() {
    // TODO
}
