package com.hisaabmate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hisaabmate.data.local.entity.GoldRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoldRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoldRate(goldRate: GoldRateEntity)

    @Delete
    suspend fun deleteGoldRate(goldRate: GoldRateEntity)

    @Query("SELECT * FROM gold_rates WHERE metal_type = :type ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRate(type: String): Flow<GoldRateEntity?>

    @Query("SELECT * FROM gold_rates ORDER BY timestamp DESC")
    fun getAllGoldRates(): Flow<List<GoldRateEntity>>
}
