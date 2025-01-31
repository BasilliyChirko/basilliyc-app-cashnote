package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun PopupMenu(
	modifier: Modifier = Modifier,
	expanded: MutableState<Boolean>,
	anchor: @Composable BoxScope.() -> Unit,
	items: @Composable ColumnScope.() -> Unit,
) {
	Box(modifier = modifier) {
		anchor()
		DropdownMenu(
			expanded = expanded.value,
			onDismissRequest = { expanded.value = false },
			content = items
		)
	}
}

@Composable
fun PopupMenu(
	modifier: Modifier = Modifier,
	expanded: Boolean,
	onDismissRequest: () -> Unit,
	anchor: @Composable BoxScope.() -> Unit,
	items: @Composable ColumnScope.() -> Unit,
) {
	Box(modifier = modifier) {
		anchor()
		DropdownMenu(
			expanded = expanded,
			onDismissRequest = onDismissRequest,
			content = items
		)
	}
}

@Composable
fun ColumnScope.PopupMenuItem(
	text: String,
	onClick: () -> Unit,
	leadingIcon: ImageVector? = null,
) {
	DropdownMenuItem(
		text = { Text(text = text) },
		onClick = onClick,
		leadingIcon = {
			if (leadingIcon != null) {
				Icon(
					imageVector = leadingIcon,
					contentDescription = text
				)
			}
		}
	)
}