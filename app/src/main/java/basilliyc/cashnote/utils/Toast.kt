package basilliyc.cashnote.utils

import android.content.Context

fun Context.showToast(
	message: String,
	duration: Int = android.widget.Toast.LENGTH_SHORT,
) {
	android.widget.Toast.makeText(this, message, duration).show()
}

fun Context.showToast(
	message: Int,
	duration: Int = android.widget.Toast.LENGTH_SHORT,
) {
	android.widget.Toast.makeText(this, message, duration).show()
}