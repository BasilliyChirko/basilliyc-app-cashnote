package basilliyc.cashnote.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.R

@Composable
fun DeleteConfirmationDialog(
	title: String,
	text: String,
	onConfirm: () -> Unit,
	onCancel: () -> Unit,
) {
	AlertDialog(
		title = {
			Text(text = title)
		},
		text = {
			Text(text = text)
		},
		onDismissRequest = onCancel,
		confirmButton = {
			TextButton(
				onClick = onConfirm,
				content = {
					Text(text = stringResource(R.string.delete_confirmation_dialog_confirm_button))
				}
			)
		},
		dismissButton = {
			TextButton(
				onClick = onCancel,
				content = {
					Text(text = stringResource(R.string.delete_confirmation_dialog_cancel_button))
				}
			)
		}
	)
}