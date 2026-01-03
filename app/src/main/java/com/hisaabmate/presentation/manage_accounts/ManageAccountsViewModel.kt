package com.hisaabmate.presentation.manage_accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.local.entity.AccountEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageAccountsViewModel @Inject constructor(
    private val repository: HisaabRepository
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = repository.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }
}
