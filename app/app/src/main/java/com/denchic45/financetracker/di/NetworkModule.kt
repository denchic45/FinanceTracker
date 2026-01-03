package com.denchic45.financetracker.di

import com.denchic45.financetracker.BuildConfig
import com.denchic45.financetracker.api.account.AccountApi
import com.denchic45.financetracker.api.auth.AuthApi
import com.denchic45.financetracker.api.auth.model.RefreshTokenRequest
import com.denchic45.financetracker.api.category.CategoryApi
import com.denchic45.financetracker.api.statistic.StatisticsApi
import com.denchic45.financetracker.api.tag.TagApi
import com.denchic45.financetracker.api.transaction.TransactionApi
import com.denchic45.financetracker.data.AppPreferences
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
                url(BuildConfig.BASE_URL)
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
                var bearerTokens: BearerTokens? = null
                bearer {

                    loadTokens {
                        val accessToken = appPreferences.accessToken.first()
                        val refreshToken = appPreferences.refreshToken.first()

                        if (accessToken != null && refreshToken != null) {
                            bearerTokens = BearerTokens(accessToken, refreshToken)
                            bearerTokens
                        } else null
                    }
                    refreshTokens {
                        get<AuthApi>().refreshToken(
                            RefreshTokenRequest(oldTokens!!.refreshToken!!)
                        ).fold(
                            ifLeft = {
                                appPreferences.clearAll()
                                null
                            },
                            ifRight = { result ->
                                bearerTokens = BearerTokens(result.token, result.refreshToken)
                                bearerTokens
                            }
                        )
                    }
                }
            }
        }
    }

    singleOf(::AuthApi)
    singleOf(::AccountApi)
    singleOf(::TransactionApi)
    singleOf(::CategoryApi)
    singleOf(::TagApi)
    singleOf(::StatisticsApi)
}