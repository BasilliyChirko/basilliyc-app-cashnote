package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.text
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupState
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.ui.components.rememberPopupState
import basilliyc.cashnote.utils.DefaultPreview

@Composable
@Preview(showBackground = true)
fun MenuRowPopupColorPreview1() = DefaultPreview {
	MenuRowPopupColor(
		title = "Title",
		subtitle = "Subtitle",
		color = FinancialColor.Blue,
		onColorSelected = {}
	)
}

@Composable
@Preview(showBackground = true)
fun MenuRowPopupColorPreview2() = DefaultPreview {
	MenuRowPopupColor(
		title = "Title",
		color = FinancialColor.Blue,
		onColorSelected = {}
	)
}

@Composable
@Preview(showBackground = true)
fun MenuRowPopupColorPreview3() = DefaultPreview {
	MenuRowPopupColor(
		title = "Title",
		color = null,
		onColorSelected = {}
	)
}

@Composable
fun MenuRowPopupColor(
	title: String,
	subtitle: String? = null,
	color: FinancialColor?,
	onColorSelected: (FinancialColor?) -> Unit,
	enabled: Boolean = true,
	contentPadding: PaddingValues = MenuRowDefaults.contentPadding,
	popupState: PopupState = rememberPopupState(),
) {
	
	DropdownMenuItem(
		modifier = Modifier,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			PopupMenu(
				state = popupState,
				anchor = {
					ColorCard(
						color = color,
						onClick = { popupState.expand() },
					)
				},
				items = {
					val colors = listOf<FinancialColor?>(null) + FinancialColor.entries
					VerticalGrid(
						modifier = Modifier
							.padding(horizontal = 16.dp),
						columns = VerticalGridCells.Fixed(2),
						horizontalSpace = 8.dp,
						itemsCount = colors.size,
					) {
						ColorCard(
							modifier = Modifier.fillMaxWidth(),
							color = colors[it],
							onClick = {
								collapse()
								onColorSelected(colors[it])
							}
						)
					}
				}
			)
		},
		onClick = { popupState.expand() },
		enabled = enabled,
		contentPadding = contentPadding,
	)
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

