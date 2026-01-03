package com.hisaabmate.presentation.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hisaabmate.R
import com.hisaabmate.presentation.components.StitchButton
import com.hisaabmate.presentation.components.StitchTextField
import com.hisaabmate.presentation.navigation.Screen

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                 if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }.using(
                    sizeTransform = null
                )
            }, label = "OnboardingTransition"
        ) { step ->
            when (step) {
                0 -> WelcomeScreen(
                    onNext = { viewModel.onEvent(OnboardingEvent.NextStep) }
                )
                1 -> ProfileEntryScreen(
                    name = uiState.userName,
                    currency = uiState.currency,
                    onNext = { name, curr ->
                        viewModel.onEvent(OnboardingEvent.UpdateProfile(name, curr))
                        viewModel.onEvent(OnboardingEvent.NextStep)
                    },
                    onBack = { viewModel.onEvent(OnboardingEvent.PreviousStep) }
                )
                2 -> LanguageSelectionScreen(
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageSelected = { viewModel.onEvent(OnboardingEvent.UpdateLanguage(it)) },
                    onNext = { viewModel.onEvent(OnboardingEvent.NextStep) },
                    onBack = { viewModel.onEvent(OnboardingEvent.PreviousStep) }
                )
                3 -> ThemeSelectionScreen(
                    selectedThemeStyle = uiState.selectedThemeStyle,
                    onThemeStyleSelected = { viewModel.onEvent(OnboardingEvent.UpdateThemeStyle(it)) },
                    onNext = { viewModel.onEvent(OnboardingEvent.NextStep) },
                    onBack = { viewModel.onEvent(OnboardingEvent.PreviousStep) }
                )
                4 -> FinalTouchScreen(
                    isDarkMode = uiState.selectedTheme == "DARK",
                    onDarkModeChanged = { isDark ->
                        viewModel.onEvent(OnboardingEvent.UpdateTheme(if (isDark) "DARK" else "LIGHT"))
                    },
                    onStart = {
                        viewModel.onEvent(OnboardingEvent.CompleteOnboarding)
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                     onBack = { viewModel.onEvent(OnboardingEvent.PreviousStep) }
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Welcome to HisaabMate",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your Privacy-First Finance Manager",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        StitchButton(
            text = "Get Started",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProfileEntryScreen(
    name: String,
    currency: String,
    onNext: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var currentName by remember { mutableStateOf(name) }
    var currentCurrency by remember { mutableStateOf(currency) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Step 1 of 4", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Let's get to know you", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        StitchTextField(
            value = currentName,
            onValueChange = { currentName = it },
            label = "Your Name / Aap ka Naam"
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Simplified currency output for now - just PKR default but editable text
        StitchTextField(
            value = currentCurrency,
            onValueChange = { currentCurrency = it },
            label = "Primary Currency (e.g. PKR)"
        )

         if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Back")
            }
            StitchButton(
                text = "Next",
                onClick = {
                    if (currentName.isBlank()) {
                        error = "Name cannot be empty"
                    } else {
                        onNext(currentName, currentCurrency)
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LanguageSelectionScreen(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
     Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Step 2 of 4", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Choose Language / Zaban", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))

         Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterChip(
                selected = selectedLanguage == "en",
                onClick = { onLanguageSelected("en") },
                label = { Text("English", modifier = Modifier.padding(8.dp)) },
                modifier = Modifier.weight(1f).height(64.dp)
            )
            FilterChip(
                selected = selectedLanguage == "ur",
                onClick = { onLanguageSelected("ur") },
                label = { Text("Urdu / اردو", modifier = Modifier.padding(8.dp)) },
                modifier = Modifier.weight(1f).height(64.dp)
            )
        }
        
         Spacer(modifier = Modifier.weight(1f))
         
         Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
             OutlinedButton(
                 onClick = onBack,
                 modifier = Modifier.weight(1f).height(56.dp)
             ) {
                 Text("Back")
             }
             StitchButton(
                 text = "Next",
                 onClick = onNext,
                 modifier = Modifier.weight(1f)
             )
         }
     }
}


@Composable
fun ThemeSelectionScreen(
    selectedThemeStyle: String,
    onThemeStyleSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Step 3 of 4", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Visual Style / Andaaz", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemePreviewCard(
                title = "Saada (Minimalist)",
                isSelected = selectedThemeStyle == "MINIMALIST",
                onClick = { onThemeStyleSelected("MINIMALIST") },
                modifier = Modifier.weight(1f),
                isPlayful = false
            )
            ThemePreviewCard(
                title = "Rang-Barangi (Playful)",
                isSelected = selectedThemeStyle == "PLAYFUL",
                onClick = { onThemeStyleSelected("PLAYFUL") },
                modifier = Modifier.weight(1f),
                isPlayful = true
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
             OutlinedButton(
                 onClick = onBack,
                 modifier = Modifier.weight(1f).height(56.dp)
             ) {
                 Text("Back")
             }
             StitchButton(
                 text = "Next",
                 onClick = onNext,
                 modifier = Modifier.weight(1f)
             )
         }
    }
}

@Composable
fun FinalTouchScreen(
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Step 4 of 4", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Final Touch", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
             modifier = Modifier.fillMaxWidth(),
             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Dark Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Reduce eye strain at night", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChanged
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
             OutlinedButton(
                 onClick = onBack,
                 modifier = Modifier.weight(1f).height(56.dp)
             ) {
                 Text("Back")
             }
             StitchButton(
                 text = "Start My Journey",
                 onClick = onStart,
                 modifier = Modifier.weight(1f)
             )
         }
    }
}
