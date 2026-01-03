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

    fun saveAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val type = selectedType ?: return@launch
            
            // Calculate effective balance for Net Worth
            val balanceValue = when (type) {
                "CARD" -> -(currentOutstanding.toDoubleOrNull() ?: 0.0) // Liability is negative
                else -> openingBalance.toDoubleOrNull() ?: 0.0
            }

            val accountType = when (type) {
                "BANK" -> AccountType.BANK
                "CARD" -> AccountType.CREDIT_CARD
                else -> AccountType.WALLET
            }

            val account = AccountEntity(
                name = name,
                account_type = accountType,
                initial_balance = openingBalance.toDoubleOrNull() ?: 0.0,
                current_balance = balanceValue,
                bank_name = if (accountType == AccountType.BANK) name else null,
                statement_date = statementDate.toIntOrNull(),
                logo_res_name = selectedBankId,
                theme_color = selectedColor
            )
            
            repository.insertAccount(account)
            onSuccess()
        }
    }
}
