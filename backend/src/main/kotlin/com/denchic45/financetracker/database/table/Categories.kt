package com.denchic45.financetracker.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Categories : LongIdTable("category", "category_id") {
    val name = text("name")
    val icon = text("icon")
    val income = bool("income")
}

class CategoryDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CategoryDao>(Categories)

    var name by Categories.name
    var icon by Categories.icon
    var income by Categories.income
}