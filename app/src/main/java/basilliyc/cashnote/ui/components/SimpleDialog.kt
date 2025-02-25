package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun SimpleDialog(
	title: String = "",
	text: String = "",
	confirmButton: String = stringResource(android.R.string.ok),
	dismissButton: String = stringResource(android.R.string.cancel),
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
	content: @Composable () -> Unit = {},
) {
	AlertDialog(
		title = {
			if (title.isNotBlank()) Text(text = title)
		},
		text = {
			Column {
				if (text.isNotBlank()) Text(text = text)
				content()
			}
		},
		onDismissRequest = onDismiss,
		confirmButton = {
			if (confirmButton.isNotBlank()) {
				TextButton(
					onClick = onConfirm,
					content = {
						Text(text = confirmButton)
					}
				)
			}
		},
		dismissButton = {
			if (dismissButton.isNotBlank()) {
				TextButton(
					onClick = onDismiss,
					content = {
						Text(text = dismissButton)
					}
				)
			}
		}
	)
}