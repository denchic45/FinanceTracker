package com.denchic45.financetracker.feature.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.right
import com.denchic45.financetracker.api.auth.model.RefreshToken
import com.denchic45.financetracker.api.auth.model.RefreshTokenRequest
import com.denchic45.financetracker.api.auth.model.SignInRequest
import com.denchic45.financetracker.api.auth.model.SignUpRequest
import com.denchic45.financetracker.database.table.RefreshTokens
import com.denchic45.financetracker.database.table.UserDao
import com.denchic45.financetracker.database.table.Users
import com.denchic45.financetracker.api.error.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.OffsetDateTime
import java.util.*

class AuthService {

    fun signUp(request: SignUpRequest): Either<EmailAlreadyUsed, Pair<UUID, String>> = transaction {
        val existingUser = UserDao.find { Users.email eq request.email }.singleOrNull()
        if (existingUser != null) return@transaction EmailAlreadyUsed.left()

        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
        val userId = UserDao.new {
            firstName = request.firstName
            lastName = request.lastName
            email = request.email
            password = hashedPassword
        }.id.value
        val refreshToken = generateAndInsertRefreshToken(userId)

        (userId to refreshToken).right()
    }


    fun findUserAndGenerateRefreshToken(request: SignInRequest): Either<ApiError, Pair<UUID, String>> = either {
        transaction {
            val user = ensureNotNull(UserDao.find(Users.email eq request.email).singleOrNull()) { WrongEmail }
            ensure(BCrypt.checkpw(request.password, user.password)) { WrongPassword }
            val userId = user.id.value
            userId to generateAndInsertRefreshToken(userId)
        }
    }

    fun refreshToken(request: RefreshTokenRequest): Either<InvalidRefreshToken, Pair<UUID, String>> = either {
        val refreshToken = ensureNotNull(findRefreshToken(request.refreshToken)) { InvalidRefreshToken }
        removeRefreshToken(request.refreshToken)
        ensure(!refreshToken.isExpired) { InvalidRefreshToken }
        refreshToken.userId to generateAndInsertRefreshToken(refreshToken.userId)
    }

    private fun generateRefreshToken(userId: UUID): RefreshToken = RefreshToken(
        userId,
        UUID.randomUUID().toString(),
        OffsetDateTime.now().plusMonths(3)
    )

    private fun generateAndInsertRefreshToken(userId: UUID): String {
        return insertRefreshToken(generateRefreshToken(userId))
    }

    fun insertRefreshToken(refreshToken: RefreshToken): String {
        return RefreshTokens.insert {
            it[userId] = refreshToken.userId
            it[token] = refreshToken.token
            it[expireAt] = refreshToken.expireAt
        }[RefreshTokens.token]
    }

    fun findRefreshToken(refreshToken: String): RefreshToken? {
        RefreshTokens.deleteWhere { RefreshTokens.expireAt less OffsetDateTime.now() }
        return RefreshTokens.selectAll().where(RefreshTokens.token eq refreshToken)
            .singleOrNull()?.let {
                RefreshToken(
                    it[RefreshTokens.userId].value,
                    it[RefreshTokens.token],
                    it[RefreshTokens.expireAt]
                )
            }
    }


    fun removeRefreshToken(refreshToken: String) {
        RefreshTokens.deleteWhere { token eq refreshToken }
    }
}