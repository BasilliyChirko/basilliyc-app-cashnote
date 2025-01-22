package basilliyc.cashnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import basilliyc.cashnote.utils.DefaultPreview

@Composable
fun DialogLoading(
	show: Boolean = true,
) {
	if (show) {
		Dialog(
			onDismissRequest = { },
			properties = DialogProperties(
				dismissOnBackPress = false,
				dismissOnClickOutside = false,
			),
			content = {
				Box(
					modifier = Modifier
						.size(100.dp)
						.background(
							color = MaterialTheme.colorScheme.background,
							shape = MaterialTheme.shapes.small,
						),
					contentAlignment = Alignment.Center,
				) {
					CircularProgressIndicator()
				}
			},
		)
	}
}

@Composable
@Preview(showBackground = true)
private fun DialogLoadingPreview() = DefaultPreview {
	DialogLoading(true)
}