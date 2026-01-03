package com.hisaabmate.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.preferences.UserPreferencesRepository
import com.hisaabmate.domain.repository.HisaabRepository
import com.hisaabmate.data.local.entity.AccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 0, // 0: Profile, 1: Language, 2: Theme, 3: Final Touch
    val userName: String = "",
    val currency: String = "PKR",
    val selectedLanguage: String = "en",
    val selectedTheme: String = "SYSTEM",
    val selectedThemeStyle: String = "MINIMALIST",
    val isNotificationPermissionGranted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val hisaabRepository: HisaabRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.UpdateProfile -> {
                _uiState.value = _uiState.value.copy(userName = event.name, currency = event.currency)
                viewModelScope.launch {
                    userPreferencesRepository.updateUserName(event.name)
                    userPreferencesRepository.updateCurrencySymbol(event.currency)
                }
            }
            is OnboardingEvent.UpdateLanguage -> {
                _uiState.value = _uiState.value.copy(selectedLanguage = event.language)
                viewModelScope.launch {
                    userPreferencesRepository.updateLanguage(event.language)
                    applyLanguage(event.language)
                }
            }
            is OnboardingEvent.UpdateTheme -> {
                _uiState.value = _uiState.value.copy(selectedTheme = event.theme)
                viewModelScope.launch {
                    userPreferencesRepository.updateTheme(event.theme)
                }
            }
            is OnboardingEvent.UpdateThemeStyle -> {
                _uiState.value = _uiState.value.copy(selectedThemeStyle = event.style)
                viewModelScope.launch {
                    userPreferencesRepository.updateThemeStyle(event.style)
                }
            }
            is OnboardingEvent.NextStep -> {
                _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
            }
            is OnboardingEvent.PreviousStep -> {
                if (_uiState.value.currentStep > 0) {
                    _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
                }
            }
            is OnboardingEvent.CompleteOnboarding -> {
                completeOnboarding()
            }
        }
    }

    private fun applyLanguage(language: String) {
        val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(if(language == "ur") "ur-PK" else "en")
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            // Explicitly save settings to ensure persistence
            userPreferencesRepository.updateTheme(_uiState.value.selectedTheme)
            userPreferencesRepository.updateThemeStyle(_uiState.value.selectedThemeStyle)
            userPreferencesRepository.updateLanguage(_uiState.value.selectedLanguage)
            userPreferencesRepository.updateUserName(_uiState.value.userName)
            userPreferencesRepository.updateCurrencySymbol(_uiState.value.currency)
            
            // Mark onboarding as complete
            userPreferencesRepository.setOnboardingComplete(true)
        }
    }
}

sealed class OnboardingEvent {
    data class UpdateProfile(val name: String, val currency: String) : OnboardingEvent()
    data class UpdateLanguage(val language: String) : OnboardingEvent()
    data class UpdateTheme(val theme: String) : OnboardingEvent()
    data class UpdateThemeStyle(val style: String) : OnboardingEvent()
    object NextStep : OnboardingEvent()
    object PreviousStep : OnboardingEvent()
    object CompleteOnboarding : OnboardingEvent()
}
