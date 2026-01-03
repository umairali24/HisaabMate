package com.hisaabmate.data.repository

import com.hisaabmate.data.local.HisaabDatabase
import com.hisaabmate.data.local.entity.AccountEntity
import com.hisaabmate.data.local.entity.GoldRateEntity
import com.hisaabmate.data.local.entity.TransactionEntity
import com.hisaabmate.domain.repository.HisaabRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HisaabRepositoryImpl @Inject constructor(
    private val db: HisaabDatabase
) : HisaabRepository {

    private val accountDao = db.accountDao
    private val transactionDao = db.transactionDao
    private val goldRateDao = db.goldRateDao

    // Accounts
    override fun getAllAccounts(): Flow<List<AccountEntity>> {
        return accountDao.getAllAccounts()
    }

    override suspend fun getAccountById(id: Int): AccountEntity? {
        return accountDao.getAccountById(id)
    }

    override suspend fun insertAccount(account: AccountEntity) {
        accountDao.insertAccount(account)
    }

    override suspend fun updateAccount(account: AccountEntity) {
        accountDao.updateAccount(account)
    }

    override suspend fun deleteAccount(account: AccountEntity) {
        accountDao.deleteAccount(account)
    }

    override fun getNetBalance(): Flow<Double> {
        return accountDao.getNetBalance()
    }

    // Transactions
    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override fun getTransactionsForAccount(accountId: Int): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsForAccount(accountId)
    }

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    override fun getCategorySpends(startDate: Long, endDate: Long): Flow<List<com.hisaabmate.data.local.dao.CategorySpend>> {
        return transactionDao.getCategorySpends(startDate, endDate)
    }

    // Goals
    override fun getAllGoals(): Flow<List<com.hisaabmate.data.local.entity.GoalEntity>> {
        return db.goalDao.getAllGoals()
    }

    override suspend fun insertGoal(goal: com.hisaabmate.data.local.entity.GoalEntity) {
        db.goalDao.insertGoal(goal)
    }

    override suspend fun updateGoal(goal: com.hisaabmate.data.local.entity.GoalEntity) {
        db.goalDao.updateGoal(goal)
    }

    override suspend fun deleteGoal(goal: com.hisaabmate.data.local.entity.GoalEntity) {
        db.goalDao.deleteGoal(goal)
    }

    override fun getTotalSavedAmount(): Flow<Double> {
        return db.goalDao.getTotalSavedAmount()
    }

    // Zakat & Rates
    override fun getLatestRate(type: String): Flow<com.hisaabmate.data.local.entity.GoldRateEntity?> {
        return db.goldRateDao.getLatestRate(type)
    }

    override suspend fun insertZakatHistory(history: com.hisaabmate.data.local.entity.ZakatHistoryEntity) {
        db.zakatDao.insertHistory(history)
    }

    override fun getZakatHistory(): Flow<List<com.hisaabmate.data.local.entity.ZakatHistoryEntity>> {
        return db.zakatDao.getAllHistory()
    }

    // Budgets
    override fun getAllBudgets(): Flow<List<com.hisaabmate.data.local.entity.BudgetEntity>> {
        return db.budgetDao.getAllBudgets()
    }

    override suspend fun upsertBudget(budget: com.hisaabmate.data.local.entity.BudgetEntity) {
        db.budgetDao.insertBudget(budget)
    }

    override suspend fun deleteBudget(budget: com.hisaabmate.data.local.entity.BudgetEntity) {
        db.budgetDao.deleteBudget(budget)
    }

    override suspend fun getBudgetByCategory(category: String): com.hisaabmate.data.local.entity.BudgetEntity? {
        return db.budgetDao.getBudgetByCategory(category)
    }

    // Gold Rates
    override fun getLatestGoldRate(): Flow<GoldRateEntity?> {
        return goldRateDao.getLatestRate("GOLD")
    }

    override suspend fun insertGoldRate(goldRate: GoldRateEntity) {
        goldRateDao.insertGoldRate(goldRate)
    }
}
