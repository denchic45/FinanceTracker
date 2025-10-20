package com.denchic45.financetracker.feature.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.denchic45.financetracker.auth.model.SignInRequest
import com.denchic45.financetracker.auth.model.SignUpRequest
import com.denchic45.financetracker.database.table.UserDao
import com.denchic45.financetracker.database.table.Users
import com.denchic45.financetracker.error.DomainError
import com.denchic45.financetracker.error.EmailAlreadyUsed
import com.denchic45.financetracker.error.WrongEmail
import com.denchic45.financetracker.error.WrongPassword
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class AuthService {

    fun signUp(request: SignUpRequest): Either<DomainError, UUID> = transaction {
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


    fun findUserId(request: SignInRequest): Either<DomainError, UUID> = transaction {
        val user = UserDao.find(Users.email eq request.email).singleOrNull()
            ?: return@transaction WrongEmail.left()

        if (!BCrypt.checkpw(request.password, user.password)) WrongPassword.left()
        else user.id.value.right()
    }
}