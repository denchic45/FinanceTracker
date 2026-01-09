package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val id: Long,
    @ColumnInfo(name = "category_name")
    val name: String,
    @ColumnInfo(name = "icon_name")
    val iconName: String,
    val income: Boolean
)