package com.hisaabmate.domain.repository

import com.hisaabmate.data.local.entity.AccountEntity
import com.hisaabmate.data.local.entity.GoldRateEntity
import com.hisaabmate.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface HisaabRepository {
    // Accounts
    fun getAllAccounts(): Flow<List<AccountEntity>>
    suspend fun getAccountById(id: Int): AccountEntity?
    suspend fun insertAccount(account: AccountEntity)
    suspend fun updateAccount(account: AccountEntity)
    suspend fun deleteAccount(account: AccountEntity)
    fun getNetBalance(): Flow<Double>

    // Transactions
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    fun getTransactionsForAccount(accountId: Int): Flow<List<TransactionEntity>>
    suspend fun insertTransaction(transaction: TransactionEntity)
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)
    fun getCategorySpends(startDate: Long, endDate: Long): Flow<List<com.hisaabmate.data.local.dao.CategorySpend>>

    // Goals
    fun getAllGoals(): Flow<List<com.hisaabmate.data.local.entity.GoalEntity>>
    suspend fun insertGoal(goal: com.hisaabmate.data.local.entity.GoalEntity)
    suspend fun updateGoal(goal: com.hisaabmate.data.local.entity.GoalEntity)
    suspend fun deleteGoal(goal: com.hisaabmate.data.local.entity.GoalEntity)
    fun getTotalSavedAmount(): Flow<Double>
    
    // Zakat & Rates
    fun getLatestRate(type: String): Flow<com.hisaabmate.data.local.entity.GoldRateEntity?>
    suspend fun insertZakatHistory(history: com.hisaabmate.data.local.entity.ZakatHistoryEntity)
    fun getZakatHistory(): Flow<List<com.hisaabmate.data.local.entity.ZakatHistoryEntity>>

    // Budgets
    fun getAllBudgets(): Flow<List<com.hisaabmate.data.local.entity.BudgetEntity>>
    suspend fun upsertBudget(budget: com.hisaabmate.data.local.entity.BudgetEntity)
    suspend fun deleteBudget(budget: com.hisaabmate.data.local.entity.BudgetEntity)
    suspend fun getBudgetByCategory(category: String): com.hisaabmate.data.local.entity.BudgetEntity?

    // Gold Rates
    fun getLatestGoldRate(): Flow<GoldRateEntity?>
    suspend fun insertGoldRate(goldRate: GoldRateEntity)
}
