package basilliyc.cashnote.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

const val KEY_NAV_REQUEST_SUCCESS = "navRequestSuccess"
const val KEY_NAV_REQUEST_RESULT = "navRequestResult"

fun <T> NavController.setResult(success: Boolean, result: T?) {
	previousBackStackEntry?.savedStateHandle?.apply {
		set(KEY_NAV_REQUEST_SUCCESS, success)
		set(KEY_NAV_REQUEST_RESULT, result)
	}
}

fun NavController.isResultSuccess(): Boolean? {
	return currentBackStackEntry?.savedStateHandle?.get<Boolean>(KEY_NAV_REQUEST_SUCCESS)
}

inline fun <reified T> NavController.getResult(): T? {
	return currentBackStackEntry?.savedStateHandle?.get<T>(KEY_NAV_REQUEST_RESULT)
}

val LocalNavController = compositionLocalOf<NavController> {
	throw IllegalStateException("There is no default NavController provided.")
}