package com.denchic45.financetracker.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Categories : LongIdTable("category", "category_id") {
    val name = text("name")
    val icon = text("icon")
    val income = bool("income")
    val ownerId = reference("user_id", Users,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

class CategoryDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CategoryDao>(Categories)

    var name by Categories.name
    var icon by Categories.icon
    var income by Categories.income
    var owner by UserDao referencedOn Categories.ownerId
}