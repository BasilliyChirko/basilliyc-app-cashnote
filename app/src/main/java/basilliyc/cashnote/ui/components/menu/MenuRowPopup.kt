package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import basilliyc.cashnote.ui.components.CardText
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupState
import basilliyc.cashnote.ui.components.rememberPopupState
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
	popupState: PopupState = rememberPopupState(),
	items: @Composable PopupState.() -> Unit,
) {
	DropdownMenuItem(
		modifier = Modifier,
		leadingIcon = leadingIcon,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			PopupMenu(
				state = popupState,
				anchor = {
					CardText(
						onClick = { popupState.expand() },
						text = value
					)
				},
				items = items,
			)
		},
		onClick = { popupState.expand() },
		enabled = enabled,
		contentPadding = contentPadding,
	)
}


