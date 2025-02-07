package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupMenuState
import basilliyc.cashnote.ui.components.rememberPopupMenuState
import basilliyc.cashnote.utils.DefaultPreview

@Composable
@Preview(showBackground = true)
private fun MenuRowPopupPreview1() = DefaultPreview {
	MenuRowPopup(
		title = "Title",
		subtitle = "Subtitle",
		value = "Value",
		items = {}
	)
}


@Composable
fun MenuRowPopup(
	title: String,
	subtitle: String? = null,
	value: String,
	enabled: Boolean = true,
	contentPadding: PaddingValues = MenuRowDefaults.contentPadding,
	items: @Composable PopupMenuState.() -> Unit,
) {
	val popupMenuState = rememberPopupMenuState()
	DropdownMenuItem(
		modifier = Modifier,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			PopupMenu(
				state = popupMenuState,
				anchor = {
					ValueCard(
						onClick = { popupMenuState.expand() },
						text = value
					)
				},
				items = items,
			)
		},
		onClick = { popupMenuState.expand() },
		enabled = enabled,
		contentPadding = contentPadding,
	)
}


@Composable
private fun ValueCard(
	modifier: Modifier = Modifier,
	text: String,
	onClick: () -> Unit,
) {
	OutlinedCard(
		modifier = modifier,
		onClick = onClick,
	) {
		Text(
			modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
			text = text,
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
		)
	}
}



