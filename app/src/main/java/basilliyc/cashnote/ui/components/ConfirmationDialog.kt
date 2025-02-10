package basilliyc.cashnote.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun ConfirmationDialog(
	title: String,
	text: String,
	confirm: String = stringResource(android.R.string.ok),
	cancel: String = stringResource(android.R.string.cancel),
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
					Text(text = confirm)
				}
			)
		},
		dismissButton = {
			TextButton(
				onClick = onCancel,
				content = {
					Text(text = cancel)
				}
			)
		}
	)
}