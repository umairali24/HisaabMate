package com.hisaabmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gold_rates")
data class GoldRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val metal_type: String = "GOLD", // "GOLD" or "SILVER"
    @androidx.room.ColumnInfo(name = "rate_per_gram") val ratePerGram: Double,
    val ratePerTola: Double,
    val timestamp: Long
)
