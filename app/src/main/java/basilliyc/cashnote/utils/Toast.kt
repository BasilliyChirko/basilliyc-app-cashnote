package basilliyc.cashnote.utils

import android.content.Context

fun Context.toast(
	message: String,
	duration: Int = android.widget.Toast.LENGTH_SHORT,
) {
	android.widget.Toast.makeText(this, message, duration).show()
}

fun Context.toast(
	message: Int,
	duration: Int = android.widget.Toast.LENGTH_SHORT,
) {
	android.widget.Toast.makeText(this, message, duration).show()
}