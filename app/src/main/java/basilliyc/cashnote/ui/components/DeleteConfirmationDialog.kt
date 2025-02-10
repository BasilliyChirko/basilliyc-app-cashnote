package basilliyc.cashnote.ui.components

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
	ConfirmationDialog(
		title = title,
		text = text,
		confirm = stringResource(R.string.delete_confirmation_dialog_confirm_button),
		onConfirm = onConfirm,
		onCancel = onCancel
	)
}