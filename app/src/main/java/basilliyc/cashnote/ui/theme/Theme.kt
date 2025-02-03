package basilliyc.cashnote.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
	primary = Purple80,
	secondary = PurpleGrey80,
	tertiary = Pink80,
	onSurfaceVariant = onSurfaceVariantNight,
)

private val LightColorScheme = lightColorScheme(
	primary = Purple40,
	secondary = PurpleGrey40,
	tertiary = Pink40,
	onSurfaceVariant = onSurfaceVariantDay,
	
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
	themeMode: ThemeMode = ThemeMode.System, // TODO replace with LocalNightMode
	// Dynamic color is available on Android 12+
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit,
) {
	CompositionLocalProvider(
		LocalThemeMode provides themeMode,
	) {
		
		val darkTheme = isDarkTheme()
		
		val colorScheme = when {
			dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
				val context = LocalContext.current
				if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
			}
			
			darkTheme -> DarkColorScheme
			else -> LightColorScheme
		}
		
		MaterialTheme(
			colorScheme = colorScheme,
			typography = Typography,
			content = content
		)
	}
	
}