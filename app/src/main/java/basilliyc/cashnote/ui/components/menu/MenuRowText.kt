package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
			Box(
				modifier = Modifier.defaultMinSize(minHeight = 48.dp),
				contentAlignment = Alignment.Center
			) {
				Text(
					modifier = Modifier
						.padding(vertical = 8.dp, horizontal = 16.dp),
					text = value,
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Bold,
				)
			}
		},
		onClick = { onClick?.invoke() },
		enabled = enabled,
		contentPadding = contentPadding,
	)
}


