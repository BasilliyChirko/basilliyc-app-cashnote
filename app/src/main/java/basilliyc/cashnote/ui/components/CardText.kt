package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CardText(
	modifier: Modifier = Modifier,
	text: String,
	onClick: (() -> Unit)? = null,
) {
	
	if (text.isEmpty()) return
	
	@Composable
	fun card(content: @Composable ColumnScope.() -> Unit) {
		if (onClick != null) {
			OutlinedCard(
				modifier = modifier,
				onClick = onClick,
				content = content,
			)
		} else {
			OutlinedCard(
				modifier = modifier,
				content = content,
			)
		}
	}
	
	card {
		Text(
			modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
			text = text,
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
		)
	}
}
