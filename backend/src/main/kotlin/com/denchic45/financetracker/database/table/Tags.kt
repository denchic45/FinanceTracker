package com.denchic45.financetracker.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Tags : LongIdTable("tag", "tag_id") {
    val name = text("name")
}

class TagDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TagDao>(Tags)

    var name by Tags.name
}