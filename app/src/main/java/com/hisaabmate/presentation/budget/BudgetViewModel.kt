package com.hisaabmate.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.local.AccountType
import com.hisaabmate.data.local.entity.BudgetEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.abs

data class BudgetUiState(
    val category: String,
    val limit: Double,
    val spent: Double,
    val progress: Float,
    val color: androidx.compose.ui.graphics.Color
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: HisaabRepository
) : ViewModel() {

    private val _safeToSpend = MutableStateFlow(0.0)
    val safeToSpend: StateFlow<Double> = _safeToSpend

    val budgetList: StateFlow<List<BudgetUiState>> = combine(
        repository.getAllBudgets(),
        repository.getCategorySpends(getStartOfMonth(), getEndOfMonth())
    ) { budgets, spends ->
        // Convert spends list to map
        val spendsMap = spends.associate { it.category to it.total }

        budgets.map { budget ->
            val spent = spendsMap[budget.category] ?: 0.0
            val progress = (spent / budget.limitAmount).toFloat().coerceIn(0f, 1f)
            BudgetUiState(
                category = budget.category,
                limit = budget.limitAmount,
                spent = spent,
                progress = progress,
                color = calculateColor(progress)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        calculateSafeToSpend()
    }

    private fun calculateSafeToSpend() {
        viewModelScope.launch {
            combine(
                repository.getAllAccounts(),
                repository.getAllBudgets(),
                repository.getTotalSavedAmount()
            ) { accounts, budgets, totalSaved ->
                val totalBankWallet = accounts.filter { 
                    it.account_type == AccountType.BANK || it.account_type == AccountType.WALLET 
                }.sumOf { it.current_balance }
                
                val totalBudgets = budgets.sumOf { it.limitAmount }
                
                val creditCardBills = accounts.filter { 
                    it.account_type == AccountType.CREDIT_CARD 
                }.sumOf { abs(it.current_balance) }

                // Safe To Spend = (Assets - Liabilities) - Budget Limits - Savings Goals
                totalBankWallet - totalBudgets - creditCardBills - totalSaved
            }.collect {
                _safeToSpend.value = it
            }
        }
    }

    fun upsertBudget(category: String, limit: Double) {
        viewModelScope.launch {
            repository.upsertBudget(BudgetEntity(category, limit))
        }
    }
    
    fun deleteBudget(category: String) {
         viewModelScope.launch {
             // Need to fetch entity first or create a dummy one with just key if DAO supports it, 
             // but our DAO delete takes entity.
             // For now, simpler to just get and delete or add a delete query by ID.
             // We'll update DAO if needed, but for now let's assume we pass the object from UI.
             // This method might need the full object.
             val budget = repository.getBudgetByCategory(category)
             if (budget != null) {
                 repository.deleteBudget(budget)
             }
         }
    }

    private fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    private fun calculateColor(progress: Float): androidx.compose.ui.graphics.Color {
        return when {
            progress < 0.5f -> androidx.compose.ui.graphics.Color.Green // Will be replaced by Theme colors
            progress < 0.8f -> androidx.compose.ui.graphics.Color.Yellow
            else -> androidx.compose.ui.graphics.Color.Red
        }
    }
}
