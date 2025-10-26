package com.denchic45.financetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.denchic45.financetracker.data.database.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Upsert(entity = AccountEntity::class)
    suspend fun upsert(entity: AccountEntity)

    @Upsert(entity = AccountEntity::class)
    suspend fun upsert(entities: List<AccountEntity>)

    @Query("SELECT * FROM account")
    fun observeAll(): Flow<List<AccountEntity>>
} 