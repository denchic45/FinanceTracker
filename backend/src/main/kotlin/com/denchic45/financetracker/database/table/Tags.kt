package com.denchic45.financetracker.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Tags : LongIdTable("tag", "tag_id") {
    val name = text("name")
    val ownerId = reference("user_id", Users,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

class TagDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TagDao>(Tags)

    var name by Tags.name
    var owner by UserDao referencedOn Tags.ownerId
}