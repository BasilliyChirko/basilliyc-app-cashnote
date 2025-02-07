package basilliyc.cashnote.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.AppPreferences
import basilliyc.cashnote.utils.EventSingleRunner
import basilliyc.cashnote.utils.Logcat
import basilliyc.cashnote.utils.inject
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

abstract class BaseViewModel() : ViewModel() {
	
	val financialManager: FinancialManager by inject()
	val preferences: AppPreferences by inject()
	
	val logcat = Logcat(this)
	
	//----------------------------------------------------------------------------------------------
	//  Event handling
	//----------------------------------------------------------------------------------------------
	
	var defaultEventSkipIfBusy = true
	var defaultEventPostDelay = true
	var defaultEventPostDelayValue = 750L
	
	val singleRunner: EventSingleRunner by lazy {
		EventSingleRunner(
			scope = viewModelScope,
			defaultEventSkipIfBusy = defaultEventSkipIfBusy,
			defaultEventPostDelay = defaultEventPostDelay,
			defaultEventPostDelayValue = defaultEventPostDelayValue,
			defaultEventContext = Dispatchers.IO
		)
	}
	
	fun schedule(
		skipIfBusy: Boolean = defaultEventSkipIfBusy,
		cancelPrevious: Boolean = false,
		context: CoroutineContext = Dispatchers.IO,
		postDelay: Boolean = defaultEventPostDelay,
		postDelayValue: Long = defaultEventPostDelayValue,
		block: suspend CoroutineScope.() -> Unit,
	) = singleRunner.schedule(
		skipIfBusy = skipIfBusy,
		cancelPrevious = cancelPrevious,
		context = context,
		postDelay = postDelay,
		postDelayValue = postDelayValue,
		block = block,
	)
	
	
}