package com.denchic45.financetracker.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TransactionDao {

    @Upsert(entity = TransactionEntity::class)
    suspend fun upsert(entities: List<TransactionEntity>)

    @Upsert(entity = TransactionEntity::class)
    suspend fun upsert(entity: TransactionEntity)

    @Transaction
    @Query(
        """
        SELECT tr.*, ca.category_id, ca.category_name category_name, ca.icon,
        ca.income, ac.account_id, ac.account_name account_name, ac.type account_type,
        ac.balance account_balance, incac.balance income_account_balance, incac.account_id, incac.account_name income_account_name,
        incac.type income_account_type
        FROM `transaction` tr
        INNER JOIN account ac ON ac.account_id == tr.account_id
        LEFT JOIN account incac ON incac.account_id == tr.income_account_id
        LEFT JOIN category ca ON ca.category_id == tr.category_id
        ORDER BY tr.datetime DESC
    """
    )
    fun observe(): PagingSource<Int, AggregatedTransactionEntity>

    @Transaction
    @Query(
        """
        SELECT tr.*, ca.category_id, ca.category_name category_name, ca.icon,
        ca.income, ac.account_id, ac.account_name account_name, ac.type account_type,
        ac.balance account_balance, incac.balance income_account_balance, incac.account_id, incac.account_name income_account_name,
        incac.type income_account_type
        FROM `transaction` tr
        INNER JOIN account ac ON ac.account_id == tr.account_id
        LEFT JOIN account incac ON incac.account_id == tr.income_account_id
        LEFT JOIN category ca ON ca.category_id == tr.category_id
        ORDER BY tr.datetime DESC
        LIMIT :limit
    """
    )
    fun getLatest(limit: Int): Flow<List<AggregatedTransactionEntity>>

    @Transaction
    @Query(
        """
        SELECT tr.*, ca.category_id, ca.category_name category_name, ca.icon,
        ca.income, ac.account_id, ac.account_name account_name, ac.type account_type,
        ac.balance account_balance, incac.balance income_account_balance, incac.account_id, incac.account_name income_account_name,
        incac.type income_account_type
        FROM `transaction` tr
        INNER JOIN account ac ON ac.account_id == tr.account_id
        LEFT JOIN account incac ON incac.account_id == tr.income_account_id
        LEFT JOIN category ca ON ca.category_id == tr.category_id
        WHERE tr.transaction_id=:transactionId
        """
    )
    fun observeById(transactionId: Long): Flow<AggregatedTransactionEntity?>

    @Query("DELETE FROM `transaction`")
    fun deleteAll()

    @Query(
        """
        DELETE FROM `transaction` WHERE transaction_id IN (:actualIds) AND datetime BETWEEN :startDate AND :endDate
    """
    )
    fun deleteByActualIdsAndDateRange(actualIds: List<Long>, startDate: Long, endDate: Long)

    @Query("DELETE FROM `transaction` WHERE transaction_id = :transactionId")
    fun deleteById(transactionId: Long)

    @Query("DELETE FROM `transaction` WHERE category_id = :categoryId")
    fun deleteByCategoryId(categoryId: Long)

    @Query("SELECT account_id FROM `transaction` WHERE transaction_id = :transactionId")
    fun getAccountIdByTransactionId(transactionId: Long): UUID
}
