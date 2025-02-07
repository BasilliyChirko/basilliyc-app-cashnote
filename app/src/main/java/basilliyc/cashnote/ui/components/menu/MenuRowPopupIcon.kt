package basilliyc.cashnote.ui.components.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.data.FinancialIcon
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.ui.components.rememberPopupMenuState
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.Nothing

@Composable
@Preview(showBackground = true)
fun MenuRowPopupIconPreview1() = DefaultPreview {
	MenuRowPopupIcon(
		title = "Title",
		subtitle = "Subtitle",
		icon = FinancialIcon.Home,
		onIconSelected = {}
	)
}

@Composable
@Preview(showBackground = true)
fun MenuRowPopupIconPreview2() = DefaultPreview {
	MenuRowPopupIcon(
		title = "Title",
		icon = FinancialIcon.Home,
		onIconSelected = {}
	)
}

@Composable
@Preview(showBackground = true)
fun MenuRowPopupIconPreview3() = DefaultPreview {
	MenuRowPopupIcon(
		title = "Title",
		icon = null,
		onIconSelected = {}
	)
}

@Composable
fun MenuRowPopupIcon(
	title: String,
	subtitle: String? = null,
	icon: FinancialIcon?,
	onIconSelected: (FinancialIcon?) -> Unit,
	enabled: Boolean = true,
	contentPadding: PaddingValues = MenuRowDefaults.contentPadding,
) {
	val popupMenuState = rememberPopupMenuState()
	
	DropdownMenuItem(
		modifier = Modifier,
		text = { MenuTitle(title = title, subtitle = subtitle) },
		trailingIcon = {
			PopupMenu(
				state = popupMenuState,
				anchor = {
					IconCard(
						icon = icon,
						onClick = { popupMenuState.expand() },
					)
				},
				items = {
					val icons = listOf<FinancialIcon?>(null) + FinancialIcon.entries
					VerticalGrid(
						modifier = Modifier.padding(horizontal = 16.dp),
						columns = VerticalGridCells.Fixed(4),
						horizontalSpace = 8.dp,
						verticalSpace = 8.dp,
						itemsCount = icons.size,
					) {
						IconCard(
							modifier = Modifier.fillMaxWidth(),
							icon = icons[it],
							onClick = {
								popupMenuState.collapse()
								onIconSelected(icons[it])
							}
						)
					}
				}
			)
			
			
		},
		onClick = { popupMenuState.expand() },
		enabled = enabled,
		contentPadding = contentPadding,
	)
	
	
}


@Composable
private fun IconCard(
	modifier: Modifier = Modifier,
	icon: FinancialIcon?,
	onClick: () -> Unit,
) {
	OutlinedCard(
		modifier = modifier,
		onClick = onClick,
	) {
		Icon(
			modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
			imageVector = icon?.imageVector ?: Icons.Nothing,
			contentDescription = icon?.name ?: "Home",
		)
	}
}



