
package com.hisaabmate.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisaabmate.R
import com.hisaabmate.presentation.components.StitchButton
import com.hisaabmate.presentation.components.StitchListItem
import com.hisaabmate.presentation.theme.PlayfulPrimary
import androidx.fragment.app.FragmentActivity

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        if (preferences == null) {
           CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Personalization
                item {
                    SettingsCategoryHeader(stringResource(R.string.personalization))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        StitchListItem(
                            title = stringResource(R.string.language),
                            subtitle = if (preferences!!.language == "ur") "Roman Urdu" else "English",
                            icon = { Icon(Icons.Default.Language, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = preferences!!.language == "ur",
                                    onCheckedChange = { isUrdu ->
                                        viewModel.updateLanguage(if (isUrdu) "ur" else "en")
                                    }
                                )
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        StitchListItem(
                            title = stringResource(R.string.theme),
                            subtitle = preferences!!.themeStyle,
                            icon = { Icon(Icons.Default.Palette, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = preferences!!.themeStyle == "PLAYFUL",
                                    onCheckedChange = { isPlayful ->
                                        viewModel.updateTheme(if (isPlayful) "PLAYFUL" else "MINIMALIST")
                                    }
                                )
                            }
                        )
                    }
                }

                // Security
                item {
                    SettingsCategoryHeader(stringResource(R.string.security))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        StitchListItem(
                            title = stringResource(R.string.biometric_lock),
                            subtitle = stringResource(R.string.biometric_subtitle),
                            icon = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = preferences!!.isBiometricEnabled,
                                    onCheckedChange = { enabled ->
                                        if (context is FragmentActivity) {
                                            viewModel.toggleBiometric(context, enabled)
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
                
                // About
                item {
                    SettingsCategoryHeader(stringResource(R.string.about))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                         StitchListItem(
                            title = "Version",
                            subtitle = "1.0.0",
                            icon = { Icon(Icons.Default.Info, contentDescription = null) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                         StitchListItem(
                            title = "Privacy Policy",
                            icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                             trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                             onClick = { /* Open Privacy Policy */ }
                        )
                    }
                }

                item {
                     Spacer(modifier = Modifier.height(32.dp))
                     StitchButton(
                         text = "Reset App",
                         onClick = { /* TODO: Reset Logic */ },
                         containerColor = MaterialTheme.colorScheme.error,
                         contentColor = MaterialTheme.colorScheme.onError,
                         modifier = Modifier.padding(16.dp)
                     )
                     Text(
                         text = "Made with ❤️ in Pakistan",
                         modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                         textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                         style = MaterialTheme.typography.labelSmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                }
            }
        }
    }
}

@Composable
fun SettingsCategoryHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}
