package com.hisaabmate.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.local.entity.AccountEntity
import com.hisaabmate.data.local.entity.TransactionEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

import com.hisaabmate.data.preferences.UserPreferencesRepository

data class DashboardUiState(
    val userName: String = "Ali",
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val accounts: List<AccountEntity> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val currentTheme: String = "MINIMALIST"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: HisaabRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getAllAccounts(),
        repository.getAllTransactions(),
        userPreferencesRepository.userPreferencesFlow
    ) { accounts, transactions, preferences ->
        val totalBalance = accounts.sumOf { it.current_balance }
        
        // Simple logic: Sum of all time transactions for Income/Expense display
        // In a real app, this would be filtered by month.
        val income = transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "DEBIT" }.sumOf { it.amount }

        DashboardUiState(
            userName = preferences.userName.ifBlank { "Ali" },
            totalBalance = totalBalance,
            totalIncome = income,
            totalExpense = expense,
            accounts = accounts,
            recentTransactions = transactions.take(10), // Show last 10
            currentTheme = preferences.themeStyle
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}
