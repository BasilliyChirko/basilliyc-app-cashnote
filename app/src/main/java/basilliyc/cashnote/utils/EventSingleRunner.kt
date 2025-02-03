package basilliyc.cashnote.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList
import kotlin.coroutines.CoroutineContext

@Composable
fun rememberSingleRunner(
	scope: CoroutineScope = rememberCoroutineScope(),
	defaultEventSkipIfBusy: Boolean = true,
	defaultEventPostDelay: Boolean = true,
	defaultEventPostDelayValue: Long = 750L,
	defaultEventContext: CoroutineContext = Dispatchers.Main,
): EventSingleRunner {
	return remember {
		EventSingleRunner(
			scope = scope,
			defaultEventSkipIfBusy = defaultEventSkipIfBusy,
			defaultEventPostDelay = defaultEventPostDelay,
			defaultEventPostDelayValue = defaultEventPostDelayValue,
			defaultEventContext = defaultEventContext,
		)
	}
}

class EventSingleRunner(
	val scope: CoroutineScope,
	var defaultEventSkipIfBusy: Boolean = false,
	var defaultEventPostDelay: Boolean = false,
	var defaultEventPostDelayValue: Long = 750L,
	var defaultEventContext: CoroutineContext = Dispatchers.IO,
) {
	
	private val eventStack = LinkedList<Event>()
	private val eventStackMutex = Mutex()
	private val eventMutex = Mutex()
	
	private data class Event(
		val context: CoroutineContext,
		val postDelay: Boolean,
		val postDelayValue: Long,
		val block: suspend CoroutineScope.() -> Unit,
	)
	
	//Guaranty single thread consume events
	fun schedule(
		skipIfBusy: Boolean = defaultEventSkipIfBusy,
		cancelPrevious: Boolean = false,
		context: CoroutineContext = defaultEventContext,
		postDelay: Boolean = defaultEventPostDelay,
		postDelayValue: Long = defaultEventPostDelayValue,
		block: suspend CoroutineScope.() -> Unit,
	) {
		if (!scope.isActive) return
		
		scope.launch {
			eventStackMutex.withLock {
				if (!scope.isActive) return@launch
				if (skipIfBusy && eventMutex.isLocked) return@launch
				if (cancelPrevious) {
					eventStack.clear()
				}
				eventStack.add(
					Event(
						context = context,
						postDelay = postDelay,
						postDelayValue = postDelayValue,
						block = block,
					)
				)
			}
			if (!scope.isActive) return@launch
			consumeEvents()
		}
	}
	
	private var consumeEventsJob: Job? = null
	private val consumeEventsMutex = Mutex()
	private fun consumeEvents() {
		scope.launch {
			if (consumeEventsMutex.isLocked) return@launch
			consumeEventsMutex.withLock {
				if (consumeEventsJob?.isActive == true) return@withLock
				consumeEventsJob = scope.launch {
					while (isActive) {
						val event = eventStackMutex.withLock {
							if (eventStack.isEmpty()) return@launch
							eventStack.pop()
						}
						
						if (!isActive) return@launch
						
						this.launch(context = event.context) {
							eventMutex.withLock {
								if (!isActive) return@launch
								
								event.block(this)
								if (!isActive) return@launch
								
								if (event.postDelay && event.postDelayValue > 0) {
									delay(event.postDelayValue)
								}
								if (!isActive) return@launch
							}
						}.join()
					}
				}
			}
		}
	}
	
}