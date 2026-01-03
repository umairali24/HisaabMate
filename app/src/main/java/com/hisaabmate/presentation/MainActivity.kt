package com.hisaabmate.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hisaabmate.presentation.dashboard.DashboardScreen
import com.hisaabmate.presentation.add_account.AddAccountScreen
import com.hisaabmate.presentation.onboarding.OnboardingScreen
import com.hisaabmate.presentation.navigation.Screen
import com.hisaabmate.presentation.theme.HisaabMateTheme
import com.hisaabmate.presentation.theme.ThemeType
import com.hisaabmate.presentation.transaction.AddTransactionScreen
import com.hisaabmate.presentation.settings.SettingsScreen
import com.hisaabmate.presentation.budget.BudgetScreen
import dagger.hilt.android.AndroidEntryPoint
import com.hisaabmate.R

@AndroidEntryPoint
class MainActivity : androidx.fragment.app.FragmentActivity() {

    @javax.inject.Inject
    lateinit var biometricManager: com.hisaabmate.domain.security.HauthBiometricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val startDestination by viewModel.startDestination.collectAsState()
            val theme by viewModel.theme.collectAsState()
            val themeStyle by viewModel.themeStyle.collectAsState()

            // Observe biometric preference for lifecycle handling
            val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
            
            // Effect to trigger auth logic if needed, but primarily handled in onResume logic below
            // Note: In Compose, onResume handling is tricky without LifecycleObserver. 
            // Better to use a LifecycleEventObserver.
            
            val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
            androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                    if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                        if (isBiometricEnabled) {
                             biometricManager.authenticate(
                                activity = this@MainActivity,
                                title = getString(R.string.biometric_lock),
                                subtitle = getString(R.string.biometric_subtitle),
                                onSuccess = { /* App Unlocked */ },
                                onError = {
                                    // If failed/cancelled, finish or minimize? 
                                    // For stricter security, finish()
                                    // finish() 
                                    // But cancelling prompt often happens accidentally. 
                                    // For now, let's just log or retry.
                                }
                            )
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val useDarkTheme = when (theme) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            val themeType = if (themeStyle == "PLAYFUL") ThemeType.PLAYFUL else ThemeType.MINIMALIST

            HisaabMateTheme(
                darkTheme = useDarkTheme,
                themeType = themeType
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    if (startDestination != null) {
                        NavHost(navController = navController, startDestination = startDestination!!) {
                            composable(Screen.Dashboard.route) {
                                DashboardScreen(navController = navController)
                            }
                            composable(Screen.AddAccount.route) {
                                AddAccountScreen(onNavigateUp = { navController.navigateUp() })
                            }
                            composable(Screen.Onboarding.route) {
                                OnboardingScreen(navController = navController)
                            }
                            composable(Screen.AddTransaction.route) {
                                AddTransactionScreen(navController = navController)
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen()
                            }
                            composable(Screen.Budget.route) {
                                BudgetScreen()
                            }
                        }
                    } else {
                        // Show Loading/Splash while checking specific preference
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
