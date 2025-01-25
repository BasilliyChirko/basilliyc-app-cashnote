package basilliyc.cashnote.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.utils.Logcat
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

abstract class BaseViewModel : ViewModel() {
	
	val logcat = Logcat(this)
	
	//----------------------------------------------------------------------------------------------
	//  Event handling
	//----------------------------------------------------------------------------------------------
	
	var defaultEventSkipIfBusy = false
	
	// Default post delay applied as 750 millis
	// to prevent actions during default fadeIn\fadeOut animation
	var defaultEventPostDelayValue = 750L
	
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
	fun handleEvent(
		skipIfBusy: Boolean = defaultEventSkipIfBusy,
		cancelPrevious: Boolean = false,
		context: CoroutineContext = Dispatchers.IO,
		postDelay: Boolean = false,
		postDelayValue: Long = defaultEventPostDelayValue,
		block: suspend CoroutineScope.() -> Unit,
	) {
		if (!viewModelScope.isActive) return
		
		viewModelScope.launch {
			eventStackMutex.withLock {
				if (!viewModelScope.isActive) return@launch
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
			if (!viewModelScope.isActive) return@launch
			consumeEvents()
		}
	}
	
	private var consumeEventsJob: Job? = null
	private val consumeEventsMutex = Mutex()
	private fun consumeEvents() {
		viewModelScope.launch {
			if (consumeEventsMutex.isLocked) return@launch
			consumeEventsMutex.withLock {
				if (consumeEventsJob?.isActive == true) return@withLock
				consumeEventsJob = viewModelScope.launch {
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