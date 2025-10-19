package com.denchic45.financetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.denchic45.financetracker.data.database.entity.CategoryEntity

@Dao
interface CategoryDao {

    @Upsert(entity = CategoryEntity::class)
    suspend fun upsert(entity: CategoryEntity)

    @Upsert(entity = CategoryEntity::class)
    suspend fun upsert(entities: List<CategoryEntity>)

    @Query("SELECT * FROM category")
    fun getAll(): List<CategoryEntity>
} 