package basilliyc.cashnote.ui.test

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.data.FinancialTransactionCategoryIcon
import basilliyc.cashnote.utils.LocalLogcat
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun DragTest() {
	val padding = PaddingValues(
		top = 24.dp,
		bottom = 32.dp,
		start = 16.dp,
		end = 16.dp,
	)
	var transactionCategories by remember {
		mutableStateOf(
			FinancialTransactionCategoryIcon.entries.mapIndexed { index, icon ->
				FinancialTransactionCategory(
					id = index.toLong(),
					name = icon.name,
					icon = icon,
				)
			}
		)
	}
	
	val logcat = LocalLogcat.current
	
	val lazyListState = rememberLazyListState()
	val dragAndDropListState = rememberDragAndDropListState(
		lazyListState = lazyListState,
		onMove = { from, to ->
			logcat.debug("On move from $from to $to")
			transactionCategories = ArrayList(transactionCategories).apply {
				reordered(from, to)
			}
		}
	)
	
	val coroutineScope = rememberCoroutineScope()
	var overscrollJob by remember { mutableStateOf<Job?>(null) }
	
	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.pointerInput(Unit) {
				detectDragGesturesAfterLongPress(
					onDragStart = { offset ->
						dragAndDropListState.onDragStart(offset)
					},
					onDragEnd = {
						dragAndDropListState.onDragInterrupted()
					},
					onDragCancel = {
						dragAndDropListState.onDragInterrupted()
					},
					onDrag = { change, dragAmount ->
						change.consume()
						dragAndDropListState.onDrag(dragAmount)
						
						if (overscrollJob?.isActive == true) return@detectDragGesturesAfterLongPress
						
						dragAndDropListState
							.checkOverscroll()
							.takeIf { it != 0f }
							?.let {
								overscrollJob = coroutineScope.launch {
									dragAndDropListState.lazyListState.scrollBy(it)
								}
							} ?: kotlin.run { overscrollJob?.cancel() }
					}
				)
			},
		state = lazyListState,
//		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		items(
			count = transactionCategories.size,
			key = { transactionCategories[it].id },
			itemContent = { index ->
				val category = transactionCategories[index]
				CategoryItem(
					category = category,
					onClick = { logcat.debug("On click $it") },
					isDragging = index == dragAndDropListState.currentIndexOfDraggedItem,
					modifier = Modifier
						.composed {
							val offsetOrNull =
								dragAndDropListState.elementDisplacement.takeIf {
									index == dragAndDropListState.currentIndexOfDraggedItem
								}
							Modifier.graphicsLayer {
								translationY = offsetOrNull ?: 0f
							}
						}
						.zIndex(if (index == dragAndDropListState.currentIndexOfDraggedItem) 1F else 0F)
				)
			}
		)
	}
}


class DraggableLazyListState(
	val lazyListState: LazyListState,
	private val onMove: (Int, Int) -> Unit,
) {
	
	private var initialDraggingElement by mutableStateOf<LazyListItemInfo?>(null)
	var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
	
	fun onDragStart(offset: Offset) {
		lazyListState.layoutInfo.visibleItemsInfo
			.firstOrNull { item -> offset.y.toInt() in item.offset..item.offsetEnd }
			?.also {
				initialDraggingElement = it
				currentIndexOfDraggedItem = it.index
			}
	}
	
	private var draggingDistance by mutableFloatStateOf(0f)
	private val initialOffsets: Pair<Int, Int>?
		get() = initialDraggingElement?.let { Pair(it.offset, it.offsetEnd) }
	
	fun onDrag(offset: Offset) {
		draggingDistance += offset.y
		
		initialOffsets?.let { (top, bottom) ->
			val startOffset = top.toFloat() + draggingDistance
			val endOffset = bottom.toFloat() + draggingDistance
			val threshold = (bottom - top) * 0.4F
			
			currentElement?.let { current ->
				lazyListState.layoutInfo.visibleItemsInfo
					.filterNot { item ->
						item.offsetEnd < startOffset || item.offset > endOffset || current.index == item.index
					}
					.firstOrNull { item ->
						val delta = startOffset - current.offset
						when {
							delta < 0F -> (item.offset + threshold) > startOffset
							delta == 0F -> false
							else -> (item.offsetEnd - threshold) < endOffset
						}
					}
			}?.let { item ->
				currentIndexOfDraggedItem?.let { currentIndex ->
					onMove.invoke(currentIndex, item.index)
				}
				currentIndexOfDraggedItem = item.index
			}
		}
	}
	
	private val currentElement: LazyListItemInfo?
		get() = currentIndexOfDraggedItem?.let {
			lazyListState.getVisibleItemInfo(it)
		}
	
	fun checkOverscroll(): Float {
		return initialDraggingElement?.let {
			val startOffset = it.offset + draggingDistance
			val endOffset = it.offsetEnd + draggingDistance
			
			return@let when {
				draggingDistance > 0 -> {
					(endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }
				}
				
				draggingDistance < 0 -> {
					(startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
				}
				
				else -> null
			}
		} ?: 0f
	}
	
	fun onDragInterrupted() {
		initialDraggingElement = null
		currentIndexOfDraggedItem = null
		draggingDistance = 0f
	}
	
	val elementDisplacement: Float?
		get() = currentIndexOfDraggedItem?.let {
			lazyListState.getVisibleItemInfo(it)
		}?.let { itemInfo ->
			(initialDraggingElement?.offset ?: 0f).toFloat() + draggingDistance - itemInfo.offset
		}
	
}

@Composable
fun rememberDragAndDropListState(
	lazyListState: LazyListState,
	onMove: (Int, Int) -> Unit,
): DraggableLazyListState {
	return remember { DraggableLazyListState(lazyListState, onMove) }
}



private val LazyListItemInfo.offsetEnd: Int
	get() = this.offset + this.size

private fun LazyListState.getVisibleItemInfo(itemPosition: Int): LazyListItemInfo? {
	return this.layoutInfo.visibleItemsInfo.getOrNull(itemPosition - this.firstVisibleItemIndex)
}


@Composable
private fun LazyItemScope.CategoryItem(
	modifier: Modifier = Modifier,
	category: FinancialTransactionCategory,
	onClick: (Long) -> Unit,
	isDragging: Boolean,
) {
	Card(
		modifier = modifier
			.let {
				if (isDragging) it
				else it.animateItem()
			}
			.fillMaxWidth(),
		onClick = { onClick(category.id) },
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
		) {
			category.icon?.imageVector?.let { icon ->
				Icon(
					imageVector = icon,
					contentDescription = category.name,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
			Text(
				modifier = Modifier.padding(8.dp),
				text = category.name,
				maxLines = 1,
			)
		}
	}
}