package basilliyc.cashnote.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	shape: Shape = ButtonDefaults.outlinedShape,
	colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
	elevation: ButtonElevation? = null,
	border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	interactionSource: MutableInteractionSource? = null,
	icon: ImageVector? = null,
	text: String = "",
) {
	OutlinedButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		shape = shape,
		colors = colors,
		elevation = elevation,
		border = border,
		contentPadding = contentPadding,
		interactionSource = interactionSource,
		content = {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				icon?.let {
					Icon(
						imageVector = icon,
						contentDescription = text
					)
					Spacer(modifier = Modifier.width(8.dp))
				}
				Text(text = text, textAlign = TextAlign.Center)
			}
		}
	)
	
}