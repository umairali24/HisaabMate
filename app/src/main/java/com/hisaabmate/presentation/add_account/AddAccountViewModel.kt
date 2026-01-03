package com.hisaabmate.presentation.add_account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.local.AccountType
import com.hisaabmate.data.local.entity.AccountEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val repository: HisaabRepository
) : ViewModel() {

    var selectedType by mutableStateOf<String?>(null) // "BANK", "WALLET", "CARD"
    
    // Bank Picker State
    var showBankPicker by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var selectedBankId by mutableStateOf<String?>(null)
    
    var accountId: Int? = null

    // Form States
    var name by mutableStateOf("")
    var accountTitle by mutableStateOf("")
    var openingBalance by mutableStateOf("") // String for input, convert to double
    var creditLimit by mutableStateOf("")
    var currentOutstanding by mutableStateOf("")
    var statementDate by mutableStateOf("")
    var selectedColor by mutableStateOf("#22C55E") // Default Green
    
    private var allBanks: List<com.hisaabmate.domain.registry.Bank> = emptyList()
    var filteredBanks by mutableStateOf<List<com.hisaabmate.domain.registry.Bank>>(emptyList())
    
    fun loadBanks(context: android.content.Context) {
        if (allBanks.isEmpty()) {
            allBanks = com.hisaabmate.domain.registry.BankRegistry.getAllBanks(context)
            filteredBanks = allBanks
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        filteredBanks = if (query.isBlank()) {
            allBanks
        } else {
            allBanks.filter { it.displayName.contains(query, ignoreCase = true) }
        }
    }

    fun onBankSelected(bank: com.hisaabmate.domain.registry.Bank) {
        selectedBankId = bank.id
        name = bank.displayName // Auto-fill name
        selectedColor = bank.defaultColor // Auto-fill color
        showBankPicker = false
    }
    
    fun onTypeSelected(type: String) {
        selectedType = type
        // Reset defaults based on type if needed
        if (type == "CARD") {
            selectedColor = "#EF4444" // Default Red for liability
        } else {
            selectedColor = "#22C55E"
        }
    }

    fun loadAccount(id: Int) {
        viewModelScope.launch {
            val account = repository.getAccountById(id) ?: return@launch
            accountId = account.id
            name = account.name
            selectedColor = account.theme_color ?: "#22C55E"

            val type = when(account.account_type) {
                AccountType.BANK -> "BANK"
                AccountType.CREDIT_CARD -> "CARD"
                AccountType.WALLET -> "BANK" // Map old WALLET to BANK as well for editing
            }
            selectedType = type

            if (account.account_type == AccountType.BANK) {
                openingBalance = account.initial_balance.toString() // Or should we show current? Typically edit shows static details.
                // However, initial_balance is what we edit.
                // But current_balance changes with transactions. Editing initial balance is tricky.
                // For simplicity, let's load initial_balance into openingBalance field.
                accountTitle = "" // Not persisted in entity actually? Checked Entity, it's not there.
                selectedBankId = account.logo_res_name
            } else if (account.account_type == AccountType.WALLET) {
                 openingBalance = account.initial_balance.toString()
            } else if (account.account_type == AccountType.CREDIT_CARD) {
                // Liability logic reverse?
                // balanceValue was -(currentOutstanding).
                // So currentOutstanding = -balanceValue
                // But account.current_balance is the net worth contribution.
                // Let's just use initial_balance if that was "Credit Limit" or "Outstanding"?
                // In save:
                // "CARD" -> -(currentOutstanding.toDoubleOrNull() ?: 0.0)
                // So if balance is -5000, outstanding is 5000.
                currentOutstanding = kotlin.math.abs(account.current_balance).toString()
                // Credit limit is not in Entity? Wait.
                // Checked Entity: id, name, type, initial, current, bank_name, statement_date, logo, color.
                // Credit Limit is missing in Entity! It was probably lost or not saved.
                // I will just ignore credit limit for now or load what I can.
                statementDate = account.statement_date?.toString() ?: ""
            }
        }
    }

    fun saveAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val type = selectedType ?: return@launch
            
            val newInitialBalance = openingBalance.toDoubleOrNull() ?: 0.0
            val newOutstanding = currentOutstanding.toDoubleOrNull() ?: 0.0

            // Map "BANK" (which now includes "Bank / Digital Wallet") to AccountType.BANK
            // This effectively migrates WALLET to BANK on edit.
            val accountType = when (type) {
                "BANK" -> AccountType.BANK
                "CARD" -> AccountType.CREDIT_CARD
                else -> AccountType.WALLET
            }

            val finalCurrentBalance: Double

            if (accountId != null && accountId != 0) {
                // Editing existing account: Preserve transaction history
                val existingAccount = repository.getAccountById(accountId!!) ?: return@launch

                if (accountType == AccountType.CREDIT_CARD) {
                    // For Credit Cards, user inputs "Current Outstanding".
                    // In our model, liability is negative balance.
                    // So if user enters 5000, balance should be -5000.
                    // We overwrite current balance because "Outstanding" is usually an absolute truth the user wants to set.
                    finalCurrentBalance = -newOutstanding
                } else {
                    // For Banks/Wallets
                    val oldInitial = existingAccount.initial_balance
                    val oldCurrent = existingAccount.current_balance
                    val delta = newInitialBalance - oldInitial
                    finalCurrentBalance = oldCurrent + delta
                }
            } else {
                // New Account
                finalCurrentBalance = when (type) {
                    "CARD" -> -newOutstanding // Liability is negative
                    else -> newInitialBalance
                }
            }

            val account = AccountEntity(
                id = accountId ?: 0,
                name = name,
                account_type = accountType,
                initial_balance = newInitialBalance,
                current_balance = finalCurrentBalance,
                bank_name = if (accountType == AccountType.BANK) name else null,
                statement_date = statementDate.toIntOrNull(),
                logo_res_name = selectedBankId,
                theme_color = selectedColor
            )
            
            if (accountId != null && accountId != 0) {
                repository.updateAccount(account)
            } else {
                repository.insertAccount(account)
            }
            onSuccess()
        }
    }
}
