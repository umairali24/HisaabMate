package com.hisaabmate.presentation.add_account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisaabmate.R
import com.hisaabmate.presentation.components.StitchButton
import com.hisaabmate.presentation.components.StitchSelectionCard
import com.hisaabmate.presentation.components.StitchTextField

@Composable
fun AddAccountScreen(
    onNavigateUp: () -> Unit,
    viewModel: AddAccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadBanks(context)
    }

    Scaffold(
        topBar = {
            // Simple Top Bar or just Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
            text = stringResource(R.string.add_account_title),
            style = MaterialTheme.typography.titleLarge
        )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            
            if (viewModel.selectedType == null) {
                // Selection Screen
                Text(
                    text = stringResource(R.string.select_account_type),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                StitchSelectionCard(
                    title = stringResource(R.string.bank_account),
                    selected = false,
                    onClick = { viewModel.onTypeSelected("BANK") },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                StitchSelectionCard(
                    title = stringResource(R.string.digital_wallet),
                    selected = false,
                    onClick = { viewModel.onTypeSelected("WALLET") },
                    icon = { Icon(Icons.Default.Wallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                StitchSelectionCard(
                    title = stringResource(R.string.credit_card),
                    selected = false,
                    onClick = { viewModel.onTypeSelected("CARD") },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            } else {
                // Details Form
                AccountDetailsForm(
                    type = viewModel.selectedType!!,
                    viewModel = viewModel,
                    onSave = {
                        viewModel.saveAccount(onSuccess = onNavigateUp)
                    }
                )
            }
        }
    }
    
    if (viewModel.showBankPicker) {
        BankSelectionSheet(
            banks = viewModel.filteredBanks,
            searchQuery = viewModel.searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onBankSelected = viewModel::onBankSelected,
            onDismiss = { viewModel.showBankPicker = false }
        )
    }
}

@Composable
fun AccountDetailsForm(
    type: String,
    viewModel: AddAccountViewModel,
    onSave: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val typeLabel = when(type) {
            "BANK" -> stringResource(R.string.bank_account)
            "WALLET" -> stringResource(R.string.digital_wallet)
            "CARD" -> stringResource(R.string.credit_card)
            else -> ""
        }
        
        Text(
            text = "$typeLabel ${stringResource(R.string.details_suffix)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Bank Selection Button (For Bank and Card)
        if (type == "BANK" || type == "CARD") {
             OutlinedCard(
                onClick = { viewModel.showBankPicker = true },
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (viewModel.name.isNotBlank() && viewModel.selectedBankId != null) {
                        Text(
                            text = viewModel.name, 
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = if(type == "BANK") "Select Bank (e.g. HBL)" else "Select Issuer (e.g. UBL)", 
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            
            /// Fallback manual entry if no bank selected, OR allow editing auto-filled name
            StitchTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = stringResource(R.string.account_name)
            )
        } else {
            // Wallet: Simple Text Field
             StitchTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = stringResource(R.string.account_name)
            )
        }

        if (type == "BANK") {
            StitchTextField(
                value = viewModel.accountTitle,
                onValueChange = { viewModel.accountTitle = it },
                label = stringResource(R.string.account_title)
            )
            StitchTextField(
                value = viewModel.openingBalance,
                onValueChange = { viewModel.openingBalance = it },
                label = stringResource(R.string.opening_balance),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
        } else if (type == "WALLET") {
             StitchTextField(
                value = viewModel.openingBalance,
                onValueChange = { viewModel.openingBalance = it },
                label = stringResource(R.string.current_balance),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
        } else if (type == "CARD") {
            StitchTextField(
                value = viewModel.creditLimit,
                onValueChange = { viewModel.creditLimit = it },
                label = stringResource(R.string.credit_limit),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            StitchTextField(
                value = viewModel.currentOutstanding,
                onValueChange = { viewModel.currentOutstanding = it },
                label = stringResource(R.string.current_outstanding_amount),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            StitchTextField(
                value = viewModel.statementDate,
                onValueChange = { viewModel.statementDate = it },
                label = stringResource(R.string.statement_date),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
        }
        
        // Color Picker
        Text(stringResource(R.string.pick_a_color), style = MaterialTheme.typography.titleSmall)
        ColorPicker(
            selectedColor = viewModel.selectedColor,
            onColorSelected = { viewModel.selectedColor = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        StitchButton(
            text = stringResource(R.string.save_account),
            onClick = onSave
        )
    }
}

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#22C55E", // Green
        "#EF4444", // Red
        "#3B82F6", // Blue
        "#F97316", // Orange
        "#A855F7", // Purple
        "#EC4899", // Pink
        "#64748B", // Slate
        "#000000"  // Black
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colors) { colorHex ->
            val color = Color(android.graphics.Color.parseColor(colorHex))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selectedColor == colorHex) 3.dp else 0.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colorHex) }
            )
        }
    }
}
