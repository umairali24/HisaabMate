package com.hisaabmate.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.local.entity.TransactionEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: HisaabRepository
) : ViewModel() {

    fun addTransaction(amount: Double, category: String, type: String) {
        viewModelScope.launch {
            val transaction = TransactionEntity(
                amount = amount,
                category = category,
                type = type, // "CREDIT" or "DEBIT"
                date = System.currentTimeMillis(),
                accountId = 1 // Default account for now
            )
            repository.insertTransaction(transaction)
            
            // If type is CREDIT, adding to balance is implicit in calculation, 
            // OR if we track explicit account balances, we'd update them here.
            // For now, simpler is better.
        }
    }
}
