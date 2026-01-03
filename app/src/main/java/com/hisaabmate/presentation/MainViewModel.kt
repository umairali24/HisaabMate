package com.hisaabmate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.preferences.UserPreferencesRepository
import com.hisaabmate.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    private val _theme = MutableStateFlow("SYSTEM")
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _themeStyle = MutableStateFlow("MINIMALIST")
    val themeStyle: StateFlow<String> = _themeStyle.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                userPreferencesRepository.isOnboardingComplete.collect { isComplete ->
                    if (_startDestination.value == null) {
                        _startDestination.value = if (isComplete) Screen.Dashboard.route else Screen.Onboarding.route
                    }
                }
            }
            
            launch {
                userPreferencesRepository.userPreferencesFlow.collect { preferences ->
                    _theme.value = preferences.theme
                    _themeStyle.value = preferences.themeStyle
                    _isBiometricEnabled.value = preferences.isBiometricEnabled
                }
            }
        }
    }
}
