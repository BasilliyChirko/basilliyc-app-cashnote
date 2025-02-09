package basilliyc.cashnote.utils

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService

@Composable
fun rememberVibrator(): Vibrator? {
	val context = LocalContext.current
	return remember { getSystemService(context, Vibrator::class.java) }
}

sealed interface Vibration {
	data object Short : Vibration
}

fun Vibrator?.vibrate(vibration: Vibration) {
	if (this == null) return
	when (vibration) {
		Vibration.Short -> vibrate(
			VibrationEffect.createOneShot(
				30,
				VibrationEffect.DEFAULT_AMPLITUDE
			)
		)
	}
}