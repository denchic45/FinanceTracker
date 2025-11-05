package com.denchic45.financetracker.api

import com.denchic45.financetracker.api.auth.AuthApi
import com.denchic45.financetracker.api.auth.model.RefreshTokenRequest
import com.denchic45.financetracker.api.auth.model.SignInRequest
import com.denchic45.financetracker.database.table.UserDao
import com.denchic45.financetracker.api.di.apiModule
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mindrot.jbcrypt.BCrypt

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class KtorClientTest : KoinTest {
    private lateinit var testApp: TestApplication
    lateinit var client: HttpClient

    val authApiOfGuest: AuthApi by inject { parametersOf(createGuestClient()) }

    private val testedUserEmail = "petrovegor@gmail.com"
    private val testedUserPassword = "6Yo,37a8;2G["

    @BeforeAll
    open fun beforeAll(): Unit = runBlocking {
        testApp = TestApplication {
            application {
                buildApplication()
            }
            environment {
                config = ApplicationConfig("application.conf")
            }
        }
        testApp.start()

        transaction {
            UserDao.new {
                firstName = "Egor"
                lastName = "Petrov"
                email = testedUserEmail
                password = BCrypt.hashpw(testedUserPassword, BCrypt.gensalt())
            }
        }
        client = createAuthenticatedClient(testedUserEmail, testedUserPassword)
    }

    @AfterAll
    open fun afterAll() = runBlocking {
        testApp.stop()
        stopKoin()
    }

    open fun Application.buildApplication() {
        loadKoinModules(apiModule)
    }

    private fun createGuestClient() = testApp.createClient {
        installContentNegotiation()
    }

    fun createAuthenticatedClient(email: String, password: String) = testApp.createClient {
        installContentNegotiation()
        install(Auth) {
            var bearerTokens = runBlocking {
                authApiOfGuest.signIn(SignInRequest(email, password)).assertedRight()
                    .let { response ->
                        BearerTokens(response.token, response.refreshToken)
                    }
            }
            bearer {
                loadTokens {
                    bearerTokens
                }
                refreshTokens {
                    val result = authApiOfGuest.refreshToken(
                        RefreshTokenRequest(oldTokens!!.refreshToken!!)
                    ).assertedRight()
                    bearerTokens = BearerTokens(result.token, result.refreshToken)
                    bearerTokens
                }
            }
        }
    }
}

private fun HttpClientConfig<out HttpClientEngineConfig>.installContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}