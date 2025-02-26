package basilliyc.cashnote.utils

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.ui.theme.colorGreen500
import basilliyc.cashnote.ui.theme.colorRed500
import java.util.Locale
import kotlin.math.absoluteValue

inline fun <T> takeIf(condition: Boolean, block: () -> T): T? {
	return if (condition) block() else null
}

inline fun <T> T.letIf(condition: (T) -> Boolean, block: (T) -> T): T {
	return if (condition(this)) block(this) else this
}

inline fun <T> T.applyIf(condition: T.() -> Boolean, block: T.() -> T): T {
	return if (condition(this)) block(this) else this
}

inline fun anyTry(block: () -> Unit) {
	try {
		block()
	} catch (ignore: Throwable) {
	}
}

inline fun <T> tryOrNull(block: () -> T): T? {
	return try {
		block()
	} catch (ignore: Throwable) {
//		Logcat("TEST").error(ignore)
		null
	}
}

inline fun <reified T> Any?.castOrNull(): T? {
	if (this == null) return null
	if (this is T) return this as T
	return null
}

private val locale = Locale("en", "US")

@SuppressLint("DefaultLocale")
fun Double.toPriceString(showPlus: Boolean, withCoins: Boolean = true, currency: FinancialCurrency? = null): String {
	val splitDot = String.format(locale, "%.2f", this.absoluteValue).split('.')
	
	val coins = splitDot[1]
	val decimal = splitDot[0].reversed().foldIndexed("") { index, string, char ->
		if (index % 3 == 2 && char != '-') {
			" $char$string"
		} else {
			"$char$string"
		}
	}.toString().trim()
	
	val symbol = when {
		this < 0 -> "-"
		showPlus && this > 0 -> "+"
		else -> ""
	}
	
	return if (withCoins) {
		"$symbol $decimal.$coins ${currency?.symbol ?: ""}".trim()
	} else {
		"$symbol $decimal ${currency?.symbol ?: ""}".trim()
	}
}

fun Double.toPriceWithCoins(withZeroCoins: Boolean = true): String {
	val string = String.format(locale, "%.2f", this)
	if (!withZeroCoins) {
		return string.replace(".00", "")
	}
	return string
}

fun Double.toPriceColor(): Color {
	return when {
		this == 0.0 -> Color.Unspecified
		this > 0 -> colorGreen500
		else -> colorRed500
	}
}

fun Double.toPercent(): String {
	val percent = (this * 100).toInt()
	return when {
		percent == 0 && this > 0 -> "<1%"
		else -> "$percent%"
	}
}

fun <T> List<T>.reordered(from: Int, to: Int): MutableList<T> {
	val mutable = if (this is MutableList) this else toMutableList()
	if (from == to) return mutable
	val element = mutable.removeAt(from)
	mutable.add(to, element)
	return mutable
}