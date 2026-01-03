package com.hisaabmate.presentation.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisaabmate.data.preferences.UserPreferencesRepository
import com.hisaabmate.domain.security.HauthBiometricManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val biometricManager: HauthBiometricManager
) : ViewModel() {

    val userPreferences = userPreferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateTheme(themeStyle: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemeStyle(themeStyle)
            // Theme update is reactive via MainViewModel, but we can force recreation if needed.
            // MainViewModel handles the actual Theme Composable update.
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateLanguage(language)
            val appLocale = LocaleListCompat.forLanguageTags(if(language == "ur") "ur-PK" else "en")
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    fun toggleBiometric(activity: FragmentActivity, enabled: Boolean) {
        if (enabled) {
            // Verify before enabling
            biometricManager.authenticate(
                activity = activity,
                title = "Confirm Biometric",
                subtitle = "Authenticate to enable App Lock",
                onSuccess = {
                    viewModelScope.launch {
                        userPreferencesRepository.setBiometricEnabled(true)
                    }
                },
                onError = { /* Handle error or show toast */ }
            )
        } else {
            // Disable immediately
            viewModelScope.launch {
                userPreferencesRepository.setBiometricEnabled(false)
            }
        }
    }
}
