package basilliyc.cashnote.ui.base

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.log
import basilliyc.cashnote.utils.rememberSingleRunner
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
	fun onResult(consume: InteractionHelper.(T) -> Unit) {
		val context = LocalContext.current
		val navController = LocalNavController.current
		val singleRunner = rememberSingleRunner()
		val interactionHelper by remember {
			derivedStateOf {
				InteractionHelper(context, navController, singleRunner)
			}
		}
		
		val result = value
		
		LaunchedEffect(result) {
			if (result != null) {
				interactionHelper.consume(result)
				onActionConsumed()
			}
		}
	}
	
}

interface ResultConsumed {
	fun onResultConsumed()
}


@Composable
fun <T> ResultEffect(state: StateResult<T>, consume: InteractionHelper.(T) -> Unit) {
	
	val context = LocalContext.current
	val navController = LocalNavController.current
	val singleRunner = rememberSingleRunner()
	val interactionHelper by remember {
		derivedStateOf {
			InteractionHelper(context, navController, singleRunner)
		}
	}
	
	val result = state.value
	
	LaunchedEffect(result) {
		log("result", result)
		if (result != null) {
			interactionHelper.consume(result)
		}
		state.onActionConsumed()
	}
	
}


@Composable
fun rememberInteractionHelper(): InteractionHelper {
	val context = LocalContext.current
	val navController = LocalNavController.current
	val singleRunner = rememberSingleRunner()
	return remember {
		derivedStateOf {
			InteractionHelper(context, navController, singleRunner)
		}
	}.value
}


@Composable
inline fun <reified T : Any> handleResult(
	result: T?,
	listener: BaseListener,
	crossinline block: InteractionHelper.(T) -> Unit,
) {
	rememberInteractionHelper().handle(result) {
		block(it)
		listener.onResultHandled()
	}
}



