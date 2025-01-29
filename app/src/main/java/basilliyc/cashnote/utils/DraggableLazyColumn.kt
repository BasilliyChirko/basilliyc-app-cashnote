package basilliyc.cashnote.utils

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

val LocalDraggableLazyColumnState = compositionLocalOf<DraggableLazyColumnState> {
	throw IllegalStateException("There is no default DraggableLazyColumnState provided.")
}

@Composable
fun DraggableLazyColumn(
	modifier: Modifier = Modifier,
	onDragStarted: () -> Unit,
	onDragMoved: (from: Int, to: Int) -> Unit,
	onDragCompleted: (from: Int, to: Int) -> Unit,
	onDragReverted: () -> Unit,
	isOverscrollEnabled: Boolean = true,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	reverseLayout: Boolean = false,
	verticalArrangement: Arrangement.Vertical =
		if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
	horizontalAlignment: Alignment.Horizontal = Alignment.Start,
	flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
	userScrollEnabled: Boolean = true,
	content: DraggableLazyColumnScope.() -> Unit,
) {
	
	var totalDraggedFrom by remember { mutableIntStateOf(-1) }
	var totalDraggedTo by remember { mutableIntStateOf(-1) }
	
	val lazyListState = rememberLazyListState()
	val draggableListState = rememberDraggableLazyColumnState(
		lazyListState = lazyListState,
		onMove = { from, to ->
			if (totalDraggedFrom == -1) {
				totalDraggedFrom = from
			}
			totalDraggedTo = to
			onDragMoved(from, to)
		},
	)
	
	val coroutineScope = rememberCoroutineScope()
	var overscrollJob by remember { mutableStateOf<Job?>(null) }
	val draggableLazyColumnScope by remember { mutableStateOf(DraggableLazyColumnScope(null)) }

	
	CompositionLocalProvider(
		LocalDraggableLazyColumnState provides draggableListState,
	) {
		
		LazyColumn(
			modifier = modifier
				.pointerInput(Unit) {
					detectDragGesturesAfterLongPress(
						onDragStart = { offset ->
							draggableListState.onDragStart(offset)
							totalDraggedFrom = -1
							totalDraggedTo = -1
							onDragStarted()
						},
						onDragEnd = {
							draggableListState.onDragInterrupted()
							onDragCompleted(totalDraggedFrom, totalDraggedTo)
							totalDraggedFrom = -1
							totalDraggedTo = -1
						},
						onDragCancel = {
							draggableListState.onDragInterrupted()
							onDragReverted()
							totalDraggedFrom = -1
							totalDraggedTo = -1
						},
						onDrag = { change, dragAmount ->
							change.consume()
							draggableListState.onDrag(dragAmount)
							
							if (isOverscrollEnabled) {
								if (overscrollJob?.isActive == true) return@detectDragGesturesAfterLongPress
								draggableListState
									.checkOverscroll()
									.takeIf { it != 0f }
									?.let {
										overscrollJob = coroutineScope.launch {
											draggableListState.lazyListState.scrollBy(it)
										}
									} ?: kotlin.run { overscrollJob?.cancel() }
							}
							
						}
					)
				},
			state = lazyListState,
			content = {
				draggableLazyColumnScope.lazyListScope = this
				content(draggableLazyColumnScope)
			},
			contentPadding = contentPadding,
			reverseLayout = reverseLayout,
			verticalArrangement = verticalArrangement,
			horizontalAlignment = horizontalAlignment,
			flingBehavior = flingBehavior,
			userScrollEnabled = userScrollEnabled,
		)
		
	}
	
}

@Composable
fun LazyItemScope.DraggableLazyColumnItem(
	modifier: Modifier = Modifier,
	index: Int,
	content: @Composable (isDragged: Boolean) -> Unit,
	animateItem: Boolean = true,
) {
	val draggableListState = LocalDraggableLazyColumnState.current
	val isDragged = draggableListState.currentIndexOfDraggedItem == index
	
	Box(
		modifier = modifier
			.composed {
				val offsetOrNull = draggableListState.elementDisplacement.takeIf { isDragged }
				Modifier.graphicsLayer {
					translationY = offsetOrNull ?: 0f
				}
			}
			.zIndex(if (isDragged) 1F else 0F)
			.let {
				if (animateItem) {
					if (isDragged) it
					else it.animateItem()
				} else it
			}
	) {
		content(isDragged)
	}
}

class DraggableLazyColumnScope(var lazyListScope: LazyListScope?) {
	fun items(
		count: Int,
		key: ((index: Int) -> Any)? = null,
		contentType: (index: Int) -> Any? = { null },
		itemContent: @Composable LazyItemScope.(index: Int, isDragged: Boolean) -> Unit,
		animateItem: Boolean = true,
	) {
		lazyListScope!!.items(count, key, contentType) { index ->
			DraggableLazyColumnItem(
				index = index,
				content = { isDragged ->
					itemContent(index, isDragged)
				},
				animateItem = animateItem
			)
		}
	}
}


@Composable
private fun rememberDraggableLazyColumnState(
	lazyListState: LazyListState,
	onMove: (Int, Int) -> Unit,
): DraggableLazyColumnState {
	return remember { DraggableLazyColumnState(lazyListState, onMove) }
}

private val LazyListItemInfo.offsetEnd: Int
	get() = this.offset + this.size

private fun LazyListState.getVisibleItemInfo(itemPosition: Int): LazyListItemInfo? {
	return this.layoutInfo.visibleItemsInfo.getOrNull(itemPosition - this.firstVisibleItemIndex)
}

class DraggableLazyColumnState(
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
