package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class TagEntity(
    @PrimaryKey
    @ColumnInfo(name = "tag_id")
    val id: Long,
    val name: String
)