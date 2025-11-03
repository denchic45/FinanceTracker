package com.denchic45.financetracker.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.Database

class DatabaseFactoryImpl(private val env: ApplicationConfig) : DatabaseFactory {

	override fun connect() {
        Database.connect(hikari())
	}

	private fun hikari(): HikariDataSource {
        val databaseUrl = env.property("database.url").getString()
        val databaseUser = env.property("database.user").getString()
        val databasePassword = env.property("database.password").getString()
        val config = HikariConfig().apply {
            jdbcUrl = databaseUrl
            username = databaseUser
            password = databasePassword
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        config.validate()
        return HikariDataSource(config)
	}
}