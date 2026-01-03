package com.hisaabmate.presentation.goals

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hisaabmate.R
import com.hisaabmate.presentation.add_account.ColorPicker
import com.hisaabmate.presentation.components.StitchButton
import com.hisaabmate.presentation.components.StitchTextField
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalSheet(
    onDismiss: () -> Unit,
    onSave: (String, Double, Long, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(System.currentTimeMillis()) }
    var deadlineText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#22C55E") }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            deadline = calendar.timeInMillis
            deadlineText = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "New Goal (Bachat ka Hadaf)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            StitchTextField(
                value = name,
                onValueChange = { name = it },
                label = "Goal Name (e.g. Bike)"
            )
            
            StitchTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = "Target Amount",
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            // Date Picker (Simple Button for now)
            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(text = if(deadlineText.isEmpty()) "Set Deadline" else "Deadline: $deadlineText")
            }

            Text("Pick a Color", style = MaterialTheme.typography.labelLarge)
            ColorPicker(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            StitchButton(
                text = "Start Saving",
                onClick = {
                    onSave(name, targetAmount.toDoubleOrNull() ?: 0.0, deadline, "star", selectedColor)
                    onDismiss()
                }
            )
        }
    }
}
