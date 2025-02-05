package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import basilliyc.cashnote.ui.theme.colorGrey99

@Composable
fun MenuTitle(
	modifier: Modifier = Modifier,
	title: String,
	subtitle: String? = null,
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.Center,
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			maxLines = 1,
		)
		if (subtitle != null) {
			Text(
				text = subtitle,
				style = MaterialTheme.typography.bodySmall,
				color = colorGrey99,
				maxLines = 3,
			)
		}
	}
}
