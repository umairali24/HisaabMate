package com.hisaabmate.presentation.zakat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.local.AccountType
import com.hisaabmate.data.local.entity.GoldRateEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ZakatViewModel @Inject constructor(
    private val repository: HisaabRepository
) : ViewModel() {

    // Wizard Step State
    var currentStep = mutableStateOf(1) // 1: Nisab, 2: Maal, 3: Debts, 4: Result

    // Step 1: Nisab Standard
    var selectedMetal = mutableStateOf("GOLD") // "GOLD" or "SILVER"
    private val _goldRate = repository.getLatestRate("GOLD")
    private val _silverRate = repository.getLatestRate("SILVER")
    
    val currentRate: StateFlow<GoldRateEntity?> = combine(_goldRate, _silverRate, kotlinx.coroutines.flow.flowOf(selectedMetal.value)) { gold, silver, type ->
        // This won't react to state change cleanly in flow builder unless we reconstruct. 
        // Better to expose both and pick in UI or use a FlatMap equivalent.
        // For simplicity, let's expose specific rates
        if(selectedMetal.value == "GOLD") gold else silver
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val goldRate = _goldRate.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val silverRate = _silverRate.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    // Step 2: Maal (Assets)
    var cashInHand = mutableStateOf("") // Manual
    var jewelryWeight = mutableStateOf("") // Manual
    var jewelryUnit = mutableStateOf("TOLA") // "TOLA" or "GRAM"
    var otherAssets = mutableStateOf("") // Manual Business/Property
    
    // Auto-pulled Cash Logic
    private val _autoCashBalance = MutableStateFlow(0.0)
    val autoCashBalance: StateFlow<Double> = _autoCashBalance

    // Step 3: Zimma-daari (Debts)
    var debts = mutableStateOf("")

    // Step 4: Result
    var zakatResult = mutableStateOf<ZakatResult?>(null)

    data class ZakatResult(
        val totalAssets: Double,
        val totalDebts: Double,
        val netWealth: Double,
        val nisabThreshold: Double,
        val isWajib: Boolean,
        val payableAmount: Double
    )

    init {
        // Auto-pull Cash on Init
        fetchAutoCash()
    }
    
    private fun fetchAutoCash() {
        viewModelScope.launch {
            val accounts = repository.getAllAccounts().first()
            val total = accounts.filter { 
                it.account_type == AccountType.BANK || it.account_type == AccountType.WALLET 
            }.sumOf { it.current_balance }
            _autoCashBalance.value = total
        }
    }

    fun calculateZakat() {
        viewModelScope.launch {
            val rateEntity = if (selectedMetal.value == "GOLD") goldRate.value else silverRate.value
            val ratePerGram = rateEntity?.ratePerGram ?: 0.0
            
            // Nisab Threshold
            // Gold: 87.48 grams, Silver: 612.36 grams
            val thresholdGrams = if (selectedMetal.value == "GOLD") 87.48 else 612.36
            val nisabValue = thresholdGrams * ratePerGram

            // Assets
            val cash = (cashInHand.value.toDoubleOrNull() ?: 0.0) + _autoCashBalance.value
            
            val weightInput = jewelryWeight.value.toDoubleOrNull() ?: 0.0
            val weightInGrams = if (jewelryUnit.value == "TOLA") weightInput * 11.664 else weightInput
            val jewelryValue = weightInGrams * ratePerGram
            
            val business = otherAssets.value.toDoubleOrNull() ?: 0.0
            
            val totalAssets = cash + jewelryValue + business
            
            // Debts
            val debtValue = debts.value.toDoubleOrNull() ?: 0.0
            
            // Net Wealth
            val netWealth = totalAssets - debtValue
            
            // Wajib Check
            val isWajib = netWealth >= nisabValue
            val payable = if (isWajib) netWealth * 0.025 else 0.0
            
            zakatResult.value = ZakatResult(
                totalAssets = totalAssets,
                totalDebts = debtValue,
                netWealth = netWealth,
                nisabThreshold = nisabValue,
                isWajib = isWajib,
                payableAmount = payable
            )
            
            // Move to Result Step
            currentStep.value = 4
            
            // Save History
            repository.insertZakatHistory(
                com.hisaabmate.data.local.entity.ZakatHistoryEntity(
                    calculation_date = System.currentTimeMillis(),
                    total_assets = totalAssets,
                    total_debts = debtValue,
                    nisab_threshold = nisabValue,
                    zakat_payable = payable
                )
            )
        }
    }
}
