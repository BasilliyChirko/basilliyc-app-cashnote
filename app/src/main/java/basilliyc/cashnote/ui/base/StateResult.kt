package basilliyc.cashnote.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.ui.base.StateResult.Handler
import basilliyc.cashnote.utils.EventSingleRunner
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.log
import basilliyc.cashnote.utils.rememberSingleRunner
import basilliyc.cashnote.utils.showToast
import kotlin.reflect.KProperty

class StateResult<T> : MutableState<T?> {
	
	@Suppress("NOTHING_TO_INLINE", "unused")
	inline operator fun setValue(
		thisObj: StateResult<T>?,
		property: KProperty<*>,
		value: T?,
	) {
		thisObj?.value = value
	}
	
	@Suppress("NOTHING_TO_INLINE", "unused")
	inline operator fun getValue(thisObj: StateResult<T>?, property: KProperty<*>): T? =
		thisObj?.value
	
	override var value: T? = null
	
	override fun component1(): T? {
		return value
	}
	
	override fun component2(): (T?) -> Unit {
		return { value = it }
	}
	
	fun onActionConsumed() {
		value = null
	}
	
	@SuppressLint("ComposableNaming")
	@Composable
	fun onResult(consume: Handler.(T) -> Unit) {
		val context = LocalContext.current
		val navController = LocalNavController.current
		val singleRunner = rememberSingleRunner()
		val handler by remember {
			derivedStateOf {
				Handler(context, navController, singleRunner)
			}
		}
		
		val result = value
		
		LaunchedEffect(result) {
			if (result != null) {
				handler.consume(result)
				onActionConsumed()
			}
		}
	}
	
	@Suppress("unused")
	class Handler(
		val context: Context,
		val navController: NavController,
		val singleRunner: EventSingleRunner,
	) {
		fun navigateForward(route: AppNavigation) {
			singleRunner.schedule {
				navController.navigate(route)
			}
		}
		
		fun navigateBack() {
			singleRunner.schedule {
				navController.popBackStack()
			}
		}
		
		fun navigateBack(route: AppNavigation, inclusive: Boolean = false) {
			singleRunner.schedule {
				navController.popBackStack(route, inclusive)
			}
		}
		
		fun showToast(
			message: String,
			duration: Int = Toast.LENGTH_SHORT,
		) {
			context.showToast(message, duration)
		}
		
		fun showToast(
			message: Int,
			duration: Int = Toast.LENGTH_SHORT,
		) {
			context.showToast(message, duration)
		}
		
		@Composable
		inline fun <reified T : Any?> handle(result: T, crossinline block: Handler.(T) -> Unit) {
			LaunchedEffect(result) {
				block(result)
			}
		}
		
	}
	
	
}

interface ResultConsumed {
	fun onResultConsumed()
}


@Composable
fun <T> ResultEffect(state: StateResult<T>, consume: StateResult.Handler.(T) -> Unit) {
	
	val context = LocalContext.current
	val navController = LocalNavController.current
	val singleRunner = rememberSingleRunner()
	val handler by remember {
		derivedStateOf {
			Handler(context, navController, singleRunner)
		}
	}
	
	val result = state.value
	
	LaunchedEffect(result) {
		log("result", result)
		if (result != null) {
			handler.consume(result)
		}
		state.onActionConsumed()
	}
	
}


@Composable
fun rememberResultHandler(): State<Handler> {
	val context = LocalContext.current
	val navController = LocalNavController.current
	val singleRunner = rememberSingleRunner()
	return remember {
		derivedStateOf {
			Handler(context, navController, singleRunner)
		}
	}
}




