package com.hisaabmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zakat_history")
data class ZakatHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val calculation_date: Long,
    val total_assets: Double,
    val total_debts: Double,
    val nisab_threshold: Double,
    val zakat_payable: Double
)
