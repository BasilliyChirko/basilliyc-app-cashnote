@file:OptIn(ExperimentalLayoutApi::class)

package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import basilliyc.cashnote.utils.LocalLogcat

sealed interface VerticalGridCells {
	data class Fixed(val count: Int) : VerticalGridCells
	data class Adaptive(val minWidth: Dp) : VerticalGridCells
}

@Composable
fun VerticalGrid(
	modifier: Modifier = Modifier,
	columns: VerticalGridCells,
	itemsCount: Int,
	verticalSpace: Dp = Dp.Unspecified,
	horizontalSpace: Dp = Dp.Unspecified,
	content: @Composable (index: Int) -> Unit,
) {
	val density = LocalDensity.current
	
	val horizontalSpacePx = remember { with(density) { horizontalSpace.toPx() } }
	var rootWidth by remember { mutableIntStateOf(0) }
	
	Column(
		modifier = modifier
			.fillMaxWidth()
			.onGloballyPositioned {
				rootWidth = it.size.width
			},
	) {
		
		val columnCount = when (columns) {
			is VerticalGridCells.Adaptive -> {
				if (rootWidth <= 0) {
					return
				} else {
					val itemWidth = with(density) { columns.minWidth.toPx() } + horizontalSpacePx
					((rootWidth + horizontalSpacePx) / itemWidth).toInt()
				}
			}
			
			is VerticalGridCells.Fixed -> columns.count
		}
		
		val rowsCount = ((itemsCount + columnCount - 1) / columnCount).takeIf { it > 0 } ?: 1
		
		var index = 0
		
		repeat(rowsCount) { row ->
			Row {
				repeat(columnCount) { column ->
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1F)
					) {
						if (index < itemsCount) {
							content(index)
						}
						index++
					}
					if (column < columnCount - 1 && horizontalSpace != Dp.Unspecified) {
						Spacer(modifier = Modifier.width(horizontalSpace))
					}
				}
			}
			if (row < rowsCount - 1 && verticalSpace != Dp.Unspecified) {
				Spacer(modifier = Modifier.height(verticalSpace))
			}
		}
		
	}
	
}