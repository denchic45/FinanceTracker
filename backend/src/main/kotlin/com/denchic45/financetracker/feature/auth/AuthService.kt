package com.denchic45.financetracker.feature.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.denchic45.financetracker.auth.AuthErrors
import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.database.table.UserDao
import com.denchic45.financetracker.database.table.Users
import com.denchic45.financetracker.feature.error.DomainError
import com.denchic45.financetracker.feature.error.EmailAlreadyUsed
import com.denchic45.financetracker.feature.error.WrongEmail
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class AuthService {

    fun signUp(request: SignUpRequest): Either<DomainError, Long> = transaction {
        val existingUser = UserDao.find { Users.email eq request.email }.singleOrNull()
        if (existingUser != null) return@transaction EmailAlreadyUsed.left()

        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
        UserDao.new {
            firstName = request.firstName
            lastName = request.lastName
            email = request.email
            password = hashedPassword
        }.id.value.right()
    }


    fun findUserId(request: SignInRequest): Either<WrongEmail, Long> = transaction {
        val user = UserDao.find(Users.email eq request.email).singleOrNull()
            ?: return@transaction WrongEmail.left()

        if (!BCrypt.checkpw(request.password, user.password)) throw BadRequestException(AuthErrors.INVALID_PASSWORD)
        user.id.value.right()
    }
}