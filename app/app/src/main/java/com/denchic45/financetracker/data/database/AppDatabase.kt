package com.denchic45.financetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.dao.CategoryDao
import com.denchic45.financetracker.data.database.dao.TransactionDao
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.data.database.entity.CategoryEntity
import com.denchic45.financetracker.data.database.entity.TransactionEntity

@Database(
    version = 1,
    entities = [TransactionEntity::class, CategoryEntity::class, AccountEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao
    abstract val categoryDao: CategoryDao
    abstract val accountDao: AccountDao
}