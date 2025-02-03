@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.R
import java.util.Calendar


@Composable
fun SimpleTimePickerDialog(
	timestamp: Long,
	onTimeSelected: (hour: Int, minute: Int) -> Unit,
	onDismiss: () -> Unit,
) {
	val initials = remember {
		Calendar.getInstance().apply {
			timeInMillis = timestamp
		}
	}
	val timePickerState = rememberTimePickerState(
		initialHour = initials.get(Calendar.HOUR_OF_DAY),
		initialMinute = initials.get(Calendar.MINUTE),
		is24Hour = true,
	)
	
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = {
				onTimeSelected(timePickerState.hour, timePickerState.minute)
				onDismiss()
			}) {
				Text(text = stringResource(R.string.account_transaction_date_picker_confirm))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.account_transaction_date_picker_cancel))
			}
		},
		text = {
			TimePicker(state = timePickerState)
		}
	)
}