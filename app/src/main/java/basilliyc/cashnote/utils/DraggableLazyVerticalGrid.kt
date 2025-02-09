package basilliyc.cashnote.utils

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

val LocalDraggableLazyVerticalGridState = compositionLocalOf<DraggableLazyVerticalGridState> {
	throw IllegalStateException("There is no default DraggableLazyVerticalGridState provided.")
}

@Composable
fun DraggableVerticalGrid(
	modifier: Modifier = Modifier,
	columns: GridCells,
	onDragStarted: (index: Int) -> Unit,
	onDragMoved: (from: Int, to: Int) -> Unit,
	onDragCompleted: (from: Int, to: Int) -> Unit,
	onDragReverted: () -> Unit,
	vibrate: Boolean = true,
	isOverscrollEnabled: Boolean = true,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	reverseLayout: Boolean = false,
	verticalArrangement: Arrangement.Vertical =
		if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
	horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
	flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
	userScrollEnabled: Boolean = true,
	content: DraggableLazyVerticalGridScope.() -> Unit,
) {
	
	var totalDraggedFrom by remember { mutableIntStateOf(-1) }
	var totalDraggedTo by remember { mutableIntStateOf(-1) }
	
	val lazyGridState = rememberLazyGridState()
	val draggableListState = rememberDraggableLazyVerticalGridState(
		lazyGridState = lazyGridState,
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
	val draggableLazyVerticalGridScope by remember {
		mutableStateOf(
			DraggableLazyVerticalGridScope(
				null
			)
		)
	}
	
	val vibrator = if (vibrate) { rememberVibrator() } else null
	
	CompositionLocalProvider(
		LocalDraggableLazyVerticalGridState provides draggableListState,
	) {
		
		LazyVerticalGrid(
			modifier = modifier
				.pointerInput(Unit) {
					detectDragGesturesAfterLongPress(
						onDragStart = { offset ->
							draggableListState.onDragStart(offset)
							totalDraggedFrom = -1
							totalDraggedTo = -1
							onDragStarted(draggableListState.currentIndexOfDraggedItem!!)
							vibrator.vibrate(Vibration.Short)
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
											draggableListState.lazyGridState.scrollBy(it)
										}
									} ?: run { overscrollJob?.cancel() }
							}
							
						}
					)
				},
			state = lazyGridState,
			content = {
				draggableLazyVerticalGridScope.lazyGridScope = this
				content(draggableLazyVerticalGridScope)
			},
			contentPadding = contentPadding,
			reverseLayout = reverseLayout,
			verticalArrangement = verticalArrangement,
			horizontalArrangement = horizontalArrangement,
			flingBehavior = flingBehavior,
			userScrollEnabled = userScrollEnabled,
			columns = columns,
		)
		
	}
	
}

@Composable
fun LazyGridItemScope.DraggableLazyVerticalGridItem(
	modifier: Modifier = Modifier,
	index: Int,
	animateItem: Boolean,
	content: @Composable (isDragged: Boolean) -> Unit,
) {
	val draggableListState = LocalDraggableLazyVerticalGridState.current
	val isDragged = draggableListState.currentIndexOfDraggedItem == index
	Box(
		modifier = modifier
			.composed {
				val offsetOrNull = draggableListState.elementDisplacement.takeIf { isDragged }
				Modifier.graphicsLayer {
					translationY = offsetOrNull?.y ?: 0f
					translationX = offsetOrNull?.x ?: 0f
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

class DraggableLazyVerticalGridScope(var lazyGridScope: LazyGridScope?) {
	fun items(
		count: Int,
		key: ((index: Int) -> Any)? = null,
		span: (LazyGridItemSpanScope.(index: Int) -> GridItemSpan)? = null,
		contentType: (index: Int) -> Any? = { null },
		itemContent: @Composable LazyGridItemScope.(index: Int, isDragged: Boolean) -> Unit,
		animateItem: Boolean = true,
	) {
		lazyGridScope!!.items(count, key, span, contentType) { index ->
			DraggableLazyVerticalGridItem(
				index = index,
				content = { isDragged ->
					itemContent(index, isDragged)
				},
				animateItem = animateItem,
			)
		}
	}
}


@Composable
private fun rememberDraggableLazyVerticalGridState(
	lazyGridState: LazyGridState,
	onMove: (Int, Int) -> Unit,
): DraggableLazyVerticalGridState {
	return remember { DraggableLazyVerticalGridState(lazyGridState, onMove) }
}

private val LazyGridItemInfo.offsetEnd: IntOffset
	get() = this.offset + IntOffset(this.size.width, this.size.height)

private fun LazyGridState.getVisibleItemInfo(itemPosition: Int): LazyGridItemInfo? {
	return this.layoutInfo.visibleItemsInfo.getOrNull(itemPosition - this.firstVisibleItemIndex)
}

private fun IntOffset.toOffset() = Offset(this.x.toFloat(), this.y.toFloat())

class DraggableLazyVerticalGridState(
	val lazyGridState: LazyGridState,
	private val onMove: (Int, Int) -> Unit,
) {
	
	private var initialDraggingElement by mutableStateOf<LazyGridItemInfo?>(null)
	var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
	
	fun onDragStart(offset: Offset) {
		lazyGridState.layoutInfo.visibleItemsInfo
			.firstOrNull { item ->
				val offsetEnd = item.offsetEnd
				offset.x.toInt() in item.offset.x..offsetEnd.x &&
						offset.y.toInt() in item.offset.y..offsetEnd.y
			}
			?.also {
				initialDraggingElement = it
				currentIndexOfDraggedItem = it.index
			}
	}
	
	private var draggingDistance by mutableStateOf(Offset.Zero)
	private val initialOffsets: Pair<Offset, Offset>?
		get() = initialDraggingElement?.let {
			val first = it.offset
			val second = it.offsetEnd
			Offset(first.x.toFloat(), first.y.toFloat()) to Offset(
				second.x.toFloat(),
				second.y.toFloat()
			)
		}
	
	fun onDrag(offset: Offset) {
		val current = currentElement ?: return
		val (initialStart, initialEnd) = initialOffsets ?: return
		
		val targetOffset = draggingDistance + offset
		
		val containerSize = lazyGridState.layoutInfo.viewportSize
		
		val farLeft = -initialStart.x
		val paddingRight = lazyGridState.layoutInfo.viewportStartOffset.absoluteValue
		val farRight = containerSize.width - initialEnd.x - paddingRight * 2
		val x = minOf(maxOf(targetOffset.x, farLeft.toFloat()), farRight.toFloat())
		
		val farTop = -initialStart.y
		val paddingBottom = lazyGridState.layoutInfo.afterContentPadding.absoluteValue
		val farBottom = containerSize.height - initialEnd.y - paddingBottom * 2
		val y = minOf(maxOf(targetOffset.y, farTop.toFloat()), farBottom.toFloat())
		
		draggingDistance = targetOffset.copy(x, y)
		
		initialOffsets?.let { (start, end) ->
			val mOffsetStart = start + draggingDistance
			val mOffsetEnd = end + draggingDistance
			
			val thresholdSquare = (current.size.width * current.size.height) * 0.5F
			
			lazyGridState.layoutInfo.visibleItemsInfo
				.filter { item ->
					
					if (current.index == item.index) return@filter false
					val offsetEnd = item.offsetEnd
					val offsetStart = item.offset
					
					if (offsetEnd.x < mOffsetStart.x || offsetStart.x > mOffsetEnd.x) return@filter false
					if (offsetEnd.y < mOffsetStart.y || offsetStart.y > mOffsetEnd.y) return@filter false
					
					true
				}
				.firstOrNull { item ->
					val startX = maxOf(item.offset.x, mOffsetStart.x.toInt())
					val endX = minOf(item.offsetEnd.x, mOffsetEnd.x.toInt())
					val startY = maxOf(item.offset.y, mOffsetStart.y.toInt())
					val endY = minOf(item.offsetEnd.y, mOffsetEnd.y.toInt())
					
					val square = (endX - startX) * (endY - startY)
					
					square >= thresholdSquare
					
				}?.let { item ->
					currentIndexOfDraggedItem?.let { currentIndex ->
						onMove.invoke(currentIndex, item.index)
					}
					currentIndexOfDraggedItem = item.index
				}
		}
	}
	
	private val currentElement: LazyGridItemInfo?
		get() = currentIndexOfDraggedItem?.let {
			lazyGridState.getVisibleItemInfo(it)
		}
	
	fun checkOverscroll(): Float {
//		return initialDraggingElement?.let {
//			val startOffset = it.offset + draggingDistance
//			val endOffset = it.offsetEnd + draggingDistance
//
//			return@let when {
//				draggingDistance > 0 -> {
//					(endOffset - lazyGridState.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }
//				}
//
//				draggingDistance < 0 -> {
//					(startOffset - lazyGridState.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
//				}
//
//				else -> null
//			}
//		} ?: 0f
		
		return 0F
	}
	
	fun onDragInterrupted() {
		initialDraggingElement = null
		currentIndexOfDraggedItem = null
		draggingDistance = Offset.Zero
	}
	
	val elementDisplacement: Offset?
		get() = currentIndexOfDraggedItem?.let {
			lazyGridState.getVisibleItemInfo(it)
		}?.let { itemInfo ->
			val initialOffsets = initialOffsets ?: return@let null
			initialOffsets.first + draggingDistance - itemInfo.offset.toOffset()
		}
	
}
