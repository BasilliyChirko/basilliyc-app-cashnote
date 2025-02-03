@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.R

@Composable
fun SimpleDatePickerDialog(
	timestamp: Long,
	onDateSelected: (Long) -> Unit,
	onDismiss: () -> Unit,
) {
	val datePickerState = rememberDatePickerState(
		initialSelectedDateMillis = timestamp
	)
	
	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = {
				onDateSelected(datePickerState.selectedDateMillis!!)
				onDismiss()
			}) {
				Text(text = stringResource(R.string.account_transaction_date_picker_confirm))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.account_transaction_date_picker_cancel))
			}
		}
	) {
		DatePicker(state = datePickerState)
	}
}