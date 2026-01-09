package com.denchic45.financetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.denchic45.financetracker.data.database.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AccountDao {

    @Upsert(entity = AccountEntity::class)
    suspend fun upsert(entity: AccountEntity)

    @Upsert(entity = AccountEntity::class)
    suspend fun upsert(entities: List<AccountEntity>)

    @Upsert(entity = AccountEntity::class)
    suspend fun upsert(entities: Set<AccountEntity>)

    @Query("SELECT * FROM account WHERE account_id = :accountId")
    fun observeById(accountId: UUID): Flow<AccountEntity?>

    @Query("SELECT * FROM account")
    fun observeAll(): Flow<List<AccountEntity>>

//    @Transaction
    @Query("DELETE FROM account WHERE account_id = :accountId")
    fun delete(accountId: UUID)

    @Query("DELETE FROM account WHERE account_id NOT IN (:ids)")
    fun deleteWhereNotIn(ids: List<UUID>)
} 