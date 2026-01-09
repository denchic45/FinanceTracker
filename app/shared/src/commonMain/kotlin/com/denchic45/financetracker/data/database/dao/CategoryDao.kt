package com.denchic45.financetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.denchic45.financetracker.data.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Upsert(entity = CategoryEntity::class)
    suspend fun upsert(entity: CategoryEntity)

    @Upsert(entity = CategoryEntity::class)
    suspend fun upsert(entities: List<CategoryEntity>)

    @Upsert(entity = CategoryEntity::class)
    suspend fun upsert(entities: Set<CategoryEntity>)

    @Query("SELECT * FROM category")
    fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM category c WHERE c.income = :income")
    fun observe(income: Boolean): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE category_id = :id")
    fun observeById(id: Long): Flow<CategoryEntity?>

    @Query("DELETE FROM category WHERE category_id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM category WHERE income = :income AND category_id NOT IN (:ids)")
    fun deleteWhereNotIn(income: Boolean, ids: List<Long>)
}