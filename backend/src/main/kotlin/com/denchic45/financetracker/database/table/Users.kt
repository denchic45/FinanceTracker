package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.database.util.exists
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Users : UUIDTable("user", "user_id") {
    val firstName = text("first_name")
    val lastName = text("last_name")
    val email = text("email")
    val password = text("password")
}

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(Users) {
        fun exists(userId: UUID): Boolean = Users.exists { Users.id eq userId }
    }

    var firstName by Users.firstName
    var lastName by Users.lastName
    var email by Users.email
    var password by Users.password
}