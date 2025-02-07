package basilliyc.cashnote.utils

import android.annotation.SuppressLint
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

inline fun <reified T> Any?.castOrNull(): T? {
	if (this == null) return null
	if (this is T) return this as T
	return null
}

private val locale = Locale("en", "US")

@SuppressLint("DefaultLocale")
fun Double.toPriceString(showPlus: Boolean): String {
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
	
	return "$symbol $decimal.$coins".trim()
}

fun Double.toPriceWithCoins(withZeroCoins: Boolean = true): String {
	val string = String.format(locale, "%.2f", this)
	if (!withZeroCoins) {
		return string.replace(".00", "")
	}
	return string
}

fun <T> List<T>.reordered(from: Int, to: Int): MutableList<T> {
	val mutable = if (this is MutableList) this else toMutableList()
	if (from == to) return mutable
	val element = mutable.removeAt(from)
	mutable.add(to, element)
	return mutable
}