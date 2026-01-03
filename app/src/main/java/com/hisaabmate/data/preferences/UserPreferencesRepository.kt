package com.hisaabmate.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException



data class UserPreferences(
    val userName: String = "",
    val currencySymbol: String = "PKR",
    val isOnboardingComplete: Boolean = false,
    val theme: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val themeStyle: String = "MINIMALIST", // MINIMALIST, PLAYFUL
    val language: String = "en", // en, ur
    val isBiometricEnabled: Boolean = false
)

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
        val THEME = stringPreferencesKey("app_theme")
        val THEME_STYLE = stringPreferencesKey("theme_style")
        val LANGUAGE = stringPreferencesKey("language")
        val IS_BIOMETRIC_ENABLED = booleanPreferencesKey("is_biometric_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                userName = preferences[Keys.USER_NAME] ?: "Ali",
                currencySymbol = preferences[Keys.CURRENCY_SYMBOL] ?: "PKR",
                isOnboardingComplete = preferences[Keys.IS_ONBOARDING_COMPLETE] ?: false,
                theme = preferences[Keys.THEME] ?: "SYSTEM",
                themeStyle = preferences[Keys.THEME_STYLE] ?: "MINIMALIST",
                language = preferences[Keys.LANGUAGE] ?: "en",
                isBiometricEnabled = preferences[Keys.IS_BIOMETRIC_ENABLED] ?: false
            )
        }

    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETE] ?: false
        }

    suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_NAME] = name
        }
    }

    suspend fun updateCurrencySymbol(symbol: String) {
        dataStore.edit { preferences ->
            preferences[Keys.CURRENCY_SYMBOL] = symbol
        }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETE] = complete
        }
    }

    suspend fun updateTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME] = theme
        }
    }

    suspend fun updateThemeStyle(style: String) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_STYLE] = style
        }
    }

    suspend fun updateLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[Keys.LANGUAGE] = language
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_BIOMETRIC_ENABLED] = enabled
        }
    }
}
