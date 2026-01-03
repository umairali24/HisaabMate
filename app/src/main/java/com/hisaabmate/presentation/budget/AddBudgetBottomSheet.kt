package com.hisaabmate.presentation.budget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hisaabmate.R
import com.hisaabmate.presentation.components.StitchButton
import com.hisaabmate.presentation.components.StitchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var category by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp) // Extra padding for navigation bar
        ) {
            Text(
                text = stringResource(R.string.add_budget),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            StitchTextField(
                value = category,
                onValueChange = { category = it },
                label = stringResource(R.string.category),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            StitchTextField(
                value = limit,
                onValueChange = { limit = it },
                label = stringResource(R.string.limit),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            StitchButton(
                text = stringResource(R.string.save_budget),
                onClick = {
                    val limitAmount = limit.toDoubleOrNull()
                    if (category.isBlank()) {
                        error = "Category cannot be empty"
                    } else if (limitAmount == null || limitAmount <= 0) {
                        error = "Invalid limit amount"
                    } else {
                        onSave(category, limitAmount)
                        onDismiss()
                    }
                }
            )
        }
    }
}
