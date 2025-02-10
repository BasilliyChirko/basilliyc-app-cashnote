package basilliyc.cashnote.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf


enum class ThemeMode {
	Night, Day, System
}

val LocalThemeMode = staticCompositionLocalOf { ThemeMode.System }

@Composable
fun isDarkTheme(): Boolean {
	return when (LocalThemeMode.current) {
		ThemeMode.Night -> true
		ThemeMode.Day -> false
		ThemeMode.System -> isSystemInDarkTheme()
	}
}

@Composable
fun CashNoteTheme(
	themeMode: ThemeMode = ThemeMode.System,
	// Dynamic color is available on Android 12+
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit,
) {
	
	CompositionLocalProvider(
		LocalThemeMode provides themeMode,
	) {
		
		(LocalActivity.current as? ComponentActivity)?.let { activity ->
			
			val activityMode = AppCompatDelegate.getDefaultNightMode()
			
			val requestedMode = when (themeMode) {
				ThemeMode.Night -> AppCompatDelegate.MODE_NIGHT_YES
				ThemeMode.Day -> AppCompatDelegate.MODE_NIGHT_NO
				ThemeMode.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
			}
			
			if (activityMode != requestedMode) {
				AppCompatDelegate.setDefaultNightMode(requestedMode)
			}
			
			
		}
		
		AppTheme(
			darkTheme = isDarkTheme(),
			dynamicColor = false,
			content = content
		)
		
	}
	
}