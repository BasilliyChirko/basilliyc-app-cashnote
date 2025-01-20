package basilliyc.cashnote.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import basilliyc.cashnote.ui.theme.CashNoteTheme

@Composable
fun DefaultPreview(content: @Composable () -> Unit) {
	CompositionLocalProvider(
		LocalNavController provides rememberNavController(),
		LocalLogcat provides Logcat(),
	) {
		CashNoteTheme {
			content()
		}
	}
}