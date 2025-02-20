package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import basilliyc.cashnote.ui.components.CardText
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
	leadingIcon: @Composable (() -> Unit)? = null,
	popupMenuState: PopupMenuState = rememberPopupMenuState(),
	items: @Composable PopupMenuState.() -> Unit,
) {
	DropdownMenuItem(
		modifier = Modifier,
		leadingIcon = leadingIcon,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			PopupMenu(
				state = popupMenuState,
				anchor = {
					CardText(
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


