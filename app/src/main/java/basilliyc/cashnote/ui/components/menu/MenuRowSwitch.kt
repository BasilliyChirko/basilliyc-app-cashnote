package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import basilliyc.cashnote.utils.DefaultPreview

@Composable
@Preview(showBackground = true)
fun MenuRowSwitchPreview1() = DefaultPreview {
	MenuRowSwitch(
		title = "Title",
		subtitle = "Subtitle",
		checked = true,
		onCheckedChange = {},
	)
}

@Composable
fun MenuRowSwitch(
	title: String,
	subtitle: String? = null,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	enabled: Boolean = true,
	contentPadding: PaddingValues = MenuRowDefaults.contentPadding,
) {
	
	DropdownMenuItem(
		modifier = Modifier,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			Switch(
				checked = checked,
				onCheckedChange = onCheckedChange,
				enabled = enabled
			)
		},
		onClick = { onCheckedChange(!checked) },
		enabled = enabled,
		contentPadding = contentPadding,
	)
	
	
}

