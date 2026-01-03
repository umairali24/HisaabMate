package com.hisaabmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hisaabmate.data.local.AccountType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val account_type: AccountType,
    val initial_balance: Double,
    val current_balance: Double,
    @androidx.room.ColumnInfo(name = "bank_name") val bank_name: String? = null,
    @androidx.room.ColumnInfo(name = "statement_date") val statement_date: Int? = null, // Day of month, 1-31
    @androidx.room.ColumnInfo(name = "logo_res_name") val logo_res_name: String? = null,
    @androidx.room.ColumnInfo(name = "theme_color") val theme_color: String? = null
)
