package com.denchic45.financetracker.di

import com.denchic45.financetracker.account.AccountApi
import com.denchic45.financetracker.auth.AuthApi
import com.denchic45.financetracker.category.CategoryApi
import com.denchic45.financetracker.data.AppPreferences
import com.denchic45.financetracker.transaction.TransactionApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val networkModule = module {
    single {
        val appPreferences = get<AppPreferences>()
        HttpClient(OkHttp) {
            defaultRequest {
                url("http://192.168.0.102:80/")
            }
            install(Logging)
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = appPreferences.accessToken.first()
                        val refreshToken = appPreferences.refreshToken.first()

                        if (accessToken != null && refreshToken != null) {
                            BearerTokens(accessToken, refreshToken)
                        } else null
                    }
                }
            }
        }
    }

    singleOf(::AccountApi)
    singleOf(::TransactionApi)
    singleOf(::CategoryApi)
    singleOf(::AuthApi)
}