package com.denchic45.financetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.denchic45.financetracker.api.account.model.AccountType
import java.util.UUID

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "account_id")
    val id: UUID,
    @ColumnInfo(name = "account_name")
    val name: String,
    val type: AccountType,
    @ColumnInfo(name = "initial_balance")
    val initialBalance: Long,
    val balance: Long,
    @ColumnInfo(name = "icon_name")
    val iconName: String,
)