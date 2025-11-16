package com.denchic45.financetracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val id: Long,
    @ColumnInfo(name = "category_name")
    val name: String,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "income")
    val income: Boolean
)