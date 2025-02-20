package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import basilliyc.cashnote.ui.components.CardText
import basilliyc.cashnote.utils.DefaultPreview

@Composable
@Preview(showBackground = true)
private fun MenuRowTextPreview1() = DefaultPreview {
	MenuRowText(
		title = "Title",
		subtitle = "Subtitle",
		value = "Value",
	)
}


@Composable
fun MenuRowText(
	title: String,
	subtitle: String? = null,
	value: String = "",
	enabled: Boolean = true,
	contentPadding: PaddingValues = MenuRowDefaults.contentPadding,
	leadingIcon: @Composable (() -> Unit)? = null,
	onClick: (() -> Unit)? = null,
) {
	DropdownMenuItem(
		modifier = Modifier,
		leadingIcon = leadingIcon,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			CardText(
				text = value,
				onClick = onClick
			)
		},
		onClick = { onClick?.invoke() },
		enabled = enabled,
		contentPadding = contentPadding,
	)
}


