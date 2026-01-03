package com.hisaabmate.presentation.zakat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisaabmate.presentation.components.StitchButton
import com.hisaabmate.presentation.components.StitchTextField
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ZakatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ZakatViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
             androidx.compose.material3.TopAppBar(
                 title = { Text("Zakat Digital Assistant") },
                 navigationIcon = {
                     androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                         androidx.compose.material3.Icon(
                             imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                             contentDescription = "Back"
                         )
                     }
                 }
             )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Steps Progress (Simple Text for now)
            Text(
                text = "Step ${viewModel.currentStep.value} of 4",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(targetState = viewModel.currentStep.value) { step ->
                when (step) {
                    1 -> StepOneNisab(viewModel)
                    2 -> StepTwoMaal(viewModel)
                    3 -> StepThreeDebts(viewModel)
                    4 -> StepFourResult(viewModel, onRestart = { viewModel.currentStep.value = 1 })
                }
            }
        }
    }
}

@Composable
fun StepOneNisab(viewModel: ZakatViewModel) {
    val goldRate by viewModel.goldRate.collectAsState()
    val silverRate by viewModel.silverRate.collectAsState()

    Column {
        Text("Select Nisab Standard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Which metal standard do you want to use?", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Gold Option
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (viewModel.selectedMetal.value == "GOLD") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ),
            onClick = { viewModel.selectedMetal.value = "GOLD" }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = viewModel.selectedMetal.value == "GOLD", onClick = null)
                Column {
                    Text("Gold (Sona)", fontWeight = FontWeight.Bold)
                    Text("Nisab: 87.48 Grams")
                    Text("Rate: ${goldRate?.ratePerGram ?: "Loading..."} / gram")
                }
            }
        }

        // Silver Option
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (viewModel.selectedMetal.value == "SILVER") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ),
            onClick = { viewModel.selectedMetal.value = "SILVER" }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = viewModel.selectedMetal.value == "SILVER", onClick = null)
                Column {
                    Text("Silver (Chandi)", fontWeight = FontWeight.Bold)
                    Text("Nisab: 612.36 Grams")
                    Text("Rate: ${silverRate?.ratePerGram ?: "Loading..."} / gram")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        StitchButton(text = "Next: Asset Details", onClick = { viewModel.currentStep.value = 2 })
    }
}

@Composable
fun StepTwoMaal(viewModel: ZakatViewModel) {
    val autoCash by viewModel.autoCashBalance.collectAsState()
    
    Column {
        Text("Maal (Assets)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Enter your zakatable assets.", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cash in Bank/Wallet (Auto-Start)", fontWeight = FontWeight.Bold)
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("en", "PK")).format(autoCash),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Fetched automatically from your accounts.", style = MaterialTheme.typography.labelSmall)
            }
        }
        
        StitchTextField(
            value = viewModel.cashInHand.value,
            onValueChange = { viewModel.cashInHand.value = it },
            label = "Cash in Hand (Extra)",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        
        // Jewelry Section
        Text("Jewelry (Gold/Silver)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StitchTextField(
                value = viewModel.jewelryWeight.value,
                onValueChange = { viewModel.jewelryWeight.value = it },
                label = "Weight",
                modifier = Modifier.weight(1f),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            androidx.compose.material3.TextButton(onClick = { 
                viewModel.jewelryUnit.value = if(viewModel.jewelryUnit.value == "TOLA") "GRAM" else "TOLA" 
            }) {
                Text(viewModel.jewelryUnit.value)
            }
        }
        
        StitchTextField(
            value = viewModel.otherAssets.value,
            onValueChange = { viewModel.otherAssets.value = it },
            label = "Other Assets (Business goods, etc)",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row {
             androidx.compose.material3.OutlinedButton(
                 onClick = { viewModel.currentStep.value = 1 },
                 modifier = Modifier.weight(1f)
             ) { Text("Back") }
             Spacer(modifier = Modifier.weight(0.1f))
             StitchButton(
                 text = "Next: Deductions", 
                 onClick = { viewModel.currentStep.value = 3 },
                 modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StepThreeDebts(viewModel: ZakatViewModel) {
    Column {
        Text("Zimma-daari (Liabilities)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Debts due immediately or within the year.", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))
        
        StitchTextField(
            value = viewModel.debts.value,
            onValueChange = { viewModel.debts.value = it },
            label = "Total Debts / Bills Due",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row {
             androidx.compose.material3.OutlinedButton(
                 onClick = { viewModel.currentStep.value = 2 },
                 modifier = Modifier.weight(1f)
             ) { Text("Back") }
             Spacer(modifier = Modifier.weight(0.1f))
             StitchButton(
                 text = "Calculate Zakat", 
                 onClick = { viewModel.calculateZakat() },
                 modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StepFourResult(viewModel: ZakatViewModel, onRestart: () -> Unit) {
    val result = viewModel.zakatResult.value ?: return
    val currency = NumberFormat.getCurrencyInstance(Locale("en", "PK"))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (result.isWajib) {
             Text("Mubarak!", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
             Text("Zakat is Wajib (Mandatory) upon you.", style = MaterialTheme.typography.bodyLarge)
             Spacer(modifier = Modifier.height(16.dp))
             
             Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                 Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                     Text("Zakat Payable", style = MaterialTheme.typography.labelLarge)
                     Text(currency.format(result.payableAmount), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                 }
             }
        } else {
            Text("Zakat Not Wajib", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Your net wealth is below the Nisab threshold.", style = MaterialTheme.typography.bodyLarge)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ResultRow("Total Assets", currency.format(result.totalAssets))
                ResultRow("Total Debts", "- ${currency.format(result.totalDebts)}")
                androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ResultRow("Net Wealth", currency.format(result.netWealth), true)
                ResultRow("Nisab Threshold", currency.format(result.nisabThreshold))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        StitchButton(text = "Start Again", onClick = onRestart)
    }
}

@Composable
fun ResultRow(label: String, value: String, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}
