package basilliyc.cashnote.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import basilliyc.cashnote.ui.theme.CashNoteTheme
import basilliyc.cashnote.utils.LocalLogcat
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.Logcat


class MainActivity : ComponentActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CompositionLocalProvider(
				LocalNavController provides rememberNavController(),
				LocalLogcat provides Logcat(this),
			) {
				val logcat = LocalLogcat.current
				
				val navController = LocalNavController.current
				val navBackStack by navController.currentBackStackEntryAsState()
				LaunchedEffect(navBackStack) {
					val backStackArguments = navBackStack?.arguments
					val destinationArguments = navBackStack?.destination?.arguments
					logcat.info(
						"Current destination: ${navBackStack?.destination?.route}",
						destinationArguments
							?.map { it.key + "=" + backStackArguments?.get(it.key) }
							?.joinToString() ?: ""
					)
				}
				
				CashNoteTheme {
					Main()
				}
				
				//todo remove after test
//				LaunchedEffect(navController) {
//					navController.navigate(
//						AppNavigation.TransactionForm(
//							accountId = 1L,
//							categoryId = 1L,
//							transactionId = null,
//						)
//					)
//				}
			
			}
			
		}
	}
}
