package com.denchic45.financetracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.denchic45.financetracker.account.model.AccountType

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "account_id")
    val id: Long,
    val name: String,
    val type: AccountType,
    val balance: Long
)