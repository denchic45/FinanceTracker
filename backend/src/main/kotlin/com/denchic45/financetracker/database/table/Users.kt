package com.denchic45.financetracker.database.table

import com.denchic45.financetracker.database.util.exists
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.Uuid

object Users : UuidTable("user", "user_id") {
    val firstName = text("first_name")
    val lastName = text("last_name")
    val email = text("email")
    val password = text("password")
}

class UserDao(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<UserDao>(Users) {
        fun exists(userId: Uuid): Boolean = Users.exists { Users.id eq userId }
    }

    var firstName by Users.firstName
    var lastName by Users.lastName
    var email by Users.email
    var password by Users.password
}