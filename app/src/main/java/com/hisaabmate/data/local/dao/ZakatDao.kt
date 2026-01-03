package com.hisaabmate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hisaabmate.data.local.entity.ZakatHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZakatDao {
    @Query("SELECT * FROM zakat_history ORDER BY calculation_date DESC")
    fun getAllHistory(): Flow<List<ZakatHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ZakatHistoryEntity)

    @Delete
    suspend fun deleteHistory(history: ZakatHistoryEntity)
}
