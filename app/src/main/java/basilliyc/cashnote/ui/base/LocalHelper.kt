package basilliyc.cashnote.ui.base

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.utils.EventSingleRunner
import basilliyc.cashnote.utils.showToast

@Suppress("unused")
class InteractionHelper(
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
	inline fun <reified T : Any> handle(result: T?, crossinline block: InteractionHelper.(T) -> Unit) {
		LaunchedEffect(result) {
			result?.let { block(it) }
		}
	}
}