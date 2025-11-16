package com.denchic45.financetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.denchic45.financetracker.data.database.entity.TagEntity
import com.denchic45.financetracker.data.database.entity.TransactionTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Upsert
    suspend fun upsert(entity: TagEntity)

    @Upsert
    suspend fun upsert(entities: List<TagEntity>)

    @Upsert
    suspend fun upsert(entities: Set<TagEntity>)

    @Query("SELECT * FROM tag")
    fun getAll(): List<TagEntity>

    @Query("SELECT * FROM tag WHERE tag_id = :id")
    suspend fun getById(id: Long): TagEntity?

    @Query("SELECT * FROM tag")
    fun observeAll(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tag WHERE tag_id = :id")
    fun observeById(id: Long): Flow<TagEntity?>

    @Query("DELETE FROM tag WHERE tag_id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionTags(crossRefs: List<TransactionTagCrossRef>)

    @Query("DELETE FROM transaction_tag WHERE transaction_id = :transactionId")
    suspend fun deleteForTransaction(transactionId: Long)

    @Query(
        """
        SELECT t.* FROM tag AS t
        INNER JOIN transaction_tag AS ttr 
            ON t.tag_id = ttr.tag_id
        WHERE ttr.transaction_id = :transactionId
    """
    )
    fun getTagsForTransaction(transactionId: Long): List<TagEntity>
}