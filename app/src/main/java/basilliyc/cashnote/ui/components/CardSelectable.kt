package basilliyc.cashnote.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun CardSelectable(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	isSelected: Boolean,
	enabled: Boolean = true,
	shape: Shape = CardDefaults.shape,
	colors: CardColors = CardDefaults.cardColors(),
	selectedColor: Color = MaterialTheme.colorScheme.primaryContainer,
	unselectedColor: Color = MaterialTheme.colorScheme.surface,
	elevation: CardElevation = CardDefaults.cardElevation(),
	interactionSource: MutableInteractionSource? = null,
	content: @Composable ColumnScope.() -> Unit,
) {
	Card(
		modifier = modifier,
		onClick = onClick,
		colors = colors.copy(
			containerColor = if (isSelected) {
				selectedColor
			} else {
				unselectedColor
			}
		),
		border = BorderStroke(
			width = 1.dp,
			color = selectedColor,
		),
		content = content,
		enabled = enabled,
		shape = shape,
		elevation = elevation,
		interactionSource = interactionSource,
	)
}
