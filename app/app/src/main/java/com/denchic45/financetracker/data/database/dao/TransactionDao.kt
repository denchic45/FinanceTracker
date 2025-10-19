package com.denchic45.financetracker.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.denchic45.financetracker.data.database.entity.AggregatedTransactionEntity
import com.denchic45.financetracker.data.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Upsert(entity = TransactionEntity::class)
    suspend fun upsert(entities: List<TransactionEntity>)

    @Upsert(entity = TransactionEntity::class)
    suspend fun upsert(entity: TransactionEntity)

    @Query(
        """
        SELECT tr.*, ca.category_id, ca.name category_name, ca.icon category_icon,
        ca.income category_income, ac.account_id, ac.name account_name, ac.type account_type,
        ac.balance account_balance, incac.balance income_account_balance, incac.account_id, incac.name income_account_name,
        incac.type income_account_type
        FROM `transaction` tr
        INNER JOIN account ac ON ac.account_id == tr.account_id
        INNER JOIN account incac ON incac.account_id == tr.income_account_id
        INNER JOIN category ca ON ca.category_id == tr.category_id
        ORDER BY tr.datetime DESC
    """
    )
    fun get(): PagingSource<Int, AggregatedTransactionEntity>

    @Query("""
        SELECT tr.*, ca.category_id, ca.name category_name, ca.icon category_icon,
        ca.income category_income, ac.account_id, ac.name account_name, ac.type account_type,
        ac.balance account_balance, incac.balance income_account_balance, incac.account_id, incac.name income_account_name,
        incac.type income_account_type
        FROM `transaction` tr
        INNER JOIN account ac ON ac.account_id == tr.account_id
        INNER JOIN account incac ON incac.account_id == tr.income_account_id
        INNER JOIN category ca ON ca.category_id == tr.category_id
        WHERE tr.transaction_id=:transactionId
        """)
    fun observeById(transactionId: Long): Flow<AggregatedTransactionEntity>

    @Query("DELETE FROM `transaction`")
    fun deleteAll()
}