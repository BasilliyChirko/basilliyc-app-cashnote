package basilliyc.cashnote.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorScheme = darkColorScheme(
	primary = Purple80,
	secondary = PurpleGrey80,
	tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
	primary = Purple40,
	secondary = PurpleGrey40,
	tertiary = Pink40,
	
	/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)


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
		
		val isNight = isDarkTheme()
		
		
		val colorScheme = when {
//			dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//				val context = LocalContext.current
//				if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//			}
			
			isNight -> DarkColorScheme
			else -> LightColorScheme
		}
		
		MaterialTheme(
			colorScheme = colorScheme,
			typography = Typography,
			content = content
		)
	}
	
}