package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.text
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.utils.DefaultPreview

@Composable
@Preview(showBackground = true)
fun MenuRowColorPreview1() = DefaultPreview {
	MenuRowColor(
		title = "Title",
		subtitle = "Subtitle",
		color = FinancialColor.Blue,
		onColorSelected = {}
	)
}

@Composable
@Preview(showBackground = true)
fun MenuRowColorPreview2() = DefaultPreview {
	MenuRowColor(
		title = "Title",
		color = FinancialColor.Blue,
		onColorSelected = {}
	)
}

@Composable
@Preview(showBackground = true)
fun MenuRowColorPreview3() = DefaultPreview {
	MenuRowColor(
		title = "Title",
		color = null,
		onColorSelected = {}
	)
}

@Composable
fun MenuRowColor(
	title: String,
	subtitle: String? = null,
	color: FinancialColor?,
	onColorSelected: (FinancialColor?) -> Unit,
	enabled: Boolean = true,
	contentPadding: PaddingValues = MenuRowDefaults.contentPadding,
) {
	
	var showDialogPicker by remember { mutableStateOf(false) }
	
	DropdownMenuItem(
		modifier = Modifier,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			ColorCard(
				color = color,
				onClick = { showDialogPicker = true },
			)
		},
		onClick = { showDialogPicker = true },
		enabled = enabled,
		contentPadding = contentPadding,
	)
	
	if (showDialogPicker) {
		
		val colors = listOf<FinancialColor?>(null) + FinancialColor.entries
		
		AlertDialog(
			onDismissRequest = { showDialogPicker = false },
			title = { Text(text = title) },
			confirmButton = {
				TextButton(
					onClick = { showDialogPicker = false },
					content = { Text(text = stringResource(android.R.string.cancel)) }
				)
			},
			text = {
				VerticalGrid(
					modifier = Modifier.padding(horizontal = 16.dp),
					columns = VerticalGridCells.Adaptive(100.dp),
					horizontalSpace = 8.dp,
					itemsCount = colors.size,
				) {
					ColorCard(
						modifier = Modifier.fillMaxWidth(),
						color = colors[it],
						onClick = {
							showDialogPicker = false
							onColorSelected(colors[it])
						}
					)
				}
			}
		)
		
		
	}
	
}


@Composable
private fun ColorCard(
	modifier: Modifier = Modifier,
	color: FinancialColor?,
	onClick: () -> Unit,
) {
	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(
			containerColor = color.color,
		),
		onClick = onClick,
	) {
		Text(
			modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
			text = color.text,
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
		)
	}
}



