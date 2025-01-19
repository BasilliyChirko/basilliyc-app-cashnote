package basilliyc.cashnote.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

abstract class NavigationItem<Argument>(
	baseRoute: String = "",
	val argument: Argument,
) {
	private val baseRoute: String = baseRoute.ifEmpty { this.javaClass.name }

}

abstract class NavigationSubject<Argument>(
	val content: @Composable (Argument) -> Unit
) {

}

fun <T> NavController.setResult(success: Boolean, result: T?) {
	previousBackStackEntry?.savedStateHandle?.apply {
		set("navRequestSuccess", success)
		set("navRequestResult", result)
	}
}


//abstract class NavigationPage<Argument>(
//
//) : NavigationSubject<Argument>() {
//
//}

//fun <T> NavController.setResult(success: Boolean, result: T?) {
//	previousBackStackEntry?.savedStateHandle?.apply {
//		set("previousPageSuccess", success)
//		set("previousPageResult", result)
//	}
//}

val LocalNavController = compositionLocalOf<NavController> {
	throw IllegalStateException("There is no default NavController provided.")
}