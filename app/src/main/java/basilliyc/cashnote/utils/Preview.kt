package basilliyc.cashnote.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import basilliyc.cashnote.AppValues
import basilliyc.cashnote.koinModules
import basilliyc.cashnote.ui.theme.CashNoteTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

private var koinStarted: Boolean = false

@Composable
fun DefaultPreview(content: @Composable () -> Unit) {
	
	if (!koinStarted) {
		val androidContext = LocalContext.current
		try {
			startKoin {
				androidContext(androidContext)
				modules(AppValues.koinModules())
			}
		} catch (ignore: Throwable) {
		}
		koinStarted = true
	}
	
	CompositionLocalProvider(
		LocalNavController provides rememberNavController(),
		LocalLogcat provides Logcat(),
	) {
		CashNoteTheme {
			content()
		}
	}
}