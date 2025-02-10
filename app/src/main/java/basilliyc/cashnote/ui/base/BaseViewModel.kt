package basilliyc.cashnote.ui.base

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.AppPreferences
import basilliyc.cashnote.utils.EventSingleRunner
import basilliyc.cashnote.utils.Logcat
import basilliyc.cashnote.utils.PermissionRequest
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
	
	
	//--------------------- Transitive Actions Handling ---------------------
	
	
	internal val activityResultRegistrationList = ArrayList<ActivityResultLateinitLauncher<*, *>>()
	internal val activitySuspendResultRegistrationList = ArrayList<ActivityRequestLauncher<*, *>>()
	
	/**
	 * Registration of onActivityResult callback inside ViewModel
	 * Callback will be register when View (Fragment or Activity) was created
	 */
	fun <I, O> registerForActivityResult(
		contract: ActivityResultContract<I, O>,
		callback: ActivityResultCallback<O>,
	): ActivityResultLateinitLauncher<I, O> {
		return ActivityResultLateinitLauncher(contract, null, callback, null)
			.also {
				activityResultRegistrationList.add(it)
			}
	}
	
	/**
	 * Registration of onActivityResult callback inside ViewModel
	 * Callback will be register when View (Fragment or Activity) was created
	 */
	fun <I, O> registerForActivityResult(
		contract: ActivityResultContract<I, O>,
		registry: ActivityResultRegistry,
		callback: ActivityResultCallback<O>,
	): ActivityResultLateinitLauncher<I, O> {
		return ActivityResultLateinitLauncher(contract, registry, callback, null)
			.also {
				activityResultRegistrationList.add(it)
			}
	}
	
	/**
	 * Registration of onActivityResult callback inside ViewModel
	 * Callback will be register when View (Fragment or Activity) was created
	 */
	fun <I, O> registerActivityRequest(
		contract: ActivityResultContract<I, O>,
		input: I? = null,
	): ActivityRequestLauncher<I, O> {
		return ActivityRequestLauncher(contract, null, input, null)
			.also {
				activitySuspendResultRegistrationList.add(it)
			}
	}
	
	
	/**
	 * Registration of onActivityResult callback inside ViewModel
	 * Callback will be register when View (Fragment or Activity) was created
	 */
	fun <I, O> registerActivityRequest(
		contract: ActivityResultContract<I, O>,
		registry: ActivityResultRegistry,
		input: I? = null,
	): ActivityRequestLauncher<I, O> {
		return ActivityRequestLauncher(contract, registry, input, null)
			.also {
				activitySuspendResultRegistrationList.add(it)
			}
	}
	
	fun registerPermissionRequest(vararg permission: String): PermissionRequest {
		return PermissionRequest(*permission).also {
			activitySuspendResultRegistrationList.add(it.requestPermissionSuspendLauncher)
		}
	}
	
	fun registerPermissionRequest(permission: List<String>): PermissionRequest {
		return PermissionRequest(permission).also {
			activitySuspendResultRegistrationList.add(it.requestPermissionSuspendLauncher)
		}
	}
	
	@SuppressLint("UnknownNullness")
	class ActivityResultLateinitLauncher<I, O>(
		private val contract: ActivityResultContract<I, O>,
		private val registry: ActivityResultRegistry?,
		private val callback: ActivityResultCallback<O>,
		private var launcher: ActivityResultLauncher<I>?,
	) {
		
		fun launch(input: I) {
			if (launcher == null) throw Throwable("Launcher are not ready yet")
			launcher?.launch(input)
		}
		
		fun launch(input: I, options: ActivityOptionsCompat?) {
			if (launcher == null) throw Throwable("Launcher are not ready yet")
			launcher?.launch(input, options)
		}
		
		fun register(fragment: Fragment) {
			launcher = if (registry != null) {
				fragment.registerForActivityResult(contract, registry, callback)
			} else {
				fragment.registerForActivityResult(contract, callback)
			}
		}
		
		fun register(activity: ComponentActivity) {
			launcher = if (registry != null) {
				activity.registerForActivityResult(contract, registry, callback)
			} else {
				activity.registerForActivityResult(contract, callback)
			}
		}
		
		@SuppressLint("ComposableNaming")
		@Composable
		fun registerOnCompose() {
			launcher = rememberLauncherForActivityResult(contract, callback::onActivityResult)
		}
		
		fun unregister() {
			launcher?.unregister()
		}
	}
	
	
	@SuppressLint("UnknownNullness")
	class ActivityRequestLauncher<I, O>(
		private val contract: ActivityResultContract<I, O>,
		private val registry: ActivityResultRegistry?,
		private val input: I?,
		private var launcher: ActivityResultLauncher<I>?,
	) {
		
		private var launchCompletableDeferred: CompletableDeferred<O>? = null
			set(value) {
				launchCompletableDeferred?.takeIf { it.isActive }?.cancel()
				field = value
			}
		
		private val callback: ActivityResultCallback<O> = ActivityResultCallback {
			launchCompletableDeferred?.takeIf { it.isActive }?.complete(it)
		}
		
		suspend fun launch(input: I? = null): O {
			if (launcher == null) throw Throwable("Launcher are not ready yet")
			val deferred = CompletableDeferred<O>().also { launchCompletableDeferred = it }
			launcher?.launch(input ?: this.input!!)
			return deferred.await()
		}
		
		suspend fun launch(input: I? = null, options: ActivityOptionsCompat?): O {
			if (launcher == null) throw Throwable("Launcher are not ready yet")
			val deferred = CompletableDeferred<O>().also { launchCompletableDeferred = it }
			launcher?.launch(input ?: this.input!!, options)
			return deferred.await()
		}
		
		fun register(fragment: Fragment) {
			launcher = if (registry != null) {
				fragment.registerForActivityResult(contract, registry, callback)
			} else {
				fragment.registerForActivityResult(contract, callback)
			}
		}
		
		fun register(activity: ComponentActivity) {
			log("register activity")
			launcher = if (registry != null) {
				activity.registerForActivityResult(contract, registry, callback)
			} else {
				activity.registerForActivityResult(contract, callback)
			}
		}
		
		@SuppressLint("ComposableNaming")
		@Composable
		fun registerOnCompose() {
			launcher = rememberLauncherForActivityResult(contract, callback::onActivityResult)
		}
		
		fun unregister() {
			launcher?.unregister()
		}
		
	}
	
	
}