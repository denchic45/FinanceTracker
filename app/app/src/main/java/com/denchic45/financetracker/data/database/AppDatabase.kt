package com.denchic45.financetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.denchic45.financetracker.data.database.dao.AccountDao
import com.denchic45.financetracker.data.database.dao.CategoryDao
import com.denchic45.financetracker.data.database.dao.TagDao
import com.denchic45.financetracker.data.database.dao.TransactionDao
import com.denchic45.financetracker.data.database.entity.AccountEntity
import com.denchic45.financetracker.data.database.entity.CategoryEntity
import com.denchic45.financetracker.data.database.entity.TagEntity
import com.denchic45.financetracker.data.database.entity.TransactionEntity
import com.denchic45.financetracker.data.database.entity.TransactionTagCrossRef

@Database(
    version = 1,
    entities = [TransactionEntity::class, CategoryEntity::class, TagEntity::class, TransactionTagCrossRef::class, AccountEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao
    abstract val categoryDao: CategoryDao
    abstract val accountDao: AccountDao
    abstract val tagDao: TagDao
}