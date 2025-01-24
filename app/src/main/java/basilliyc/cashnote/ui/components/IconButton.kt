package basilliyc.cashnote.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconButton(
	onClick: () -> Unit,
	imageVector: ImageVector,
	contentDescription: String,
) {
	IconButton(
		onClick = onClick
	) {
		Icon(
			imageVector = imageVector,
			contentDescription = contentDescription
		)
	}
}