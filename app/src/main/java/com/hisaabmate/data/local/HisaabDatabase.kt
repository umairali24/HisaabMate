package com.hisaabmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hisaabmate.data.local.dao.AccountDao
import com.hisaabmate.data.local.dao.BudgetDao
import com.hisaabmate.data.local.dao.GoldRateDao
import com.hisaabmate.data.local.dao.TransactionDao
import com.hisaabmate.data.local.entity.AccountEntity
import com.hisaabmate.data.local.entity.BudgetEntity
import com.hisaabmate.data.local.entity.GoldRateEntity
import com.hisaabmate.data.local.entity.TransactionEntity

@TypeConverters(Converters::class)
@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        GoldRateEntity::class,
        BudgetEntity::class,
        com.hisaabmate.data.local.entity.GoalEntity::class,
        com.hisaabmate.data.local.entity.ZakatHistoryEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class HisaabDatabase : RoomDatabase() {
    abstract val accountDao: AccountDao
    abstract val transactionDao: TransactionDao
    abstract val goldRateDao: GoldRateDao
    abstract val budgetDao: BudgetDao
    abstract val goalDao: com.hisaabmate.data.local.dao.GoalDao
    abstract val zakatDao: com.hisaabmate.data.local.dao.ZakatDao
}
