@file:Suppress("unused")

package basilliyc.cashnote.utils

import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlin.apply
import kotlin.collections.forEach
import kotlin.collections.joinToString
import kotlin.math.min
import kotlin.stackTraceToString
import kotlin.text.isNotEmpty
import kotlin.text.substring

val LocalLogcat = compositionLocalOf {
	Logcat()
}

class Logcat(tag: String = "") {
	
	constructor(clazz: Class<*>) : this(tag = clazz.simpleName)
	
	constructor(any: Any) : this(tag = any::class.java.simpleName)
	
	private val tag = "Logcat" + if (tag.isNotEmpty()) "-$tag" else ""
	
	companion object {
		const val LOG_MAX_LENGTH = 4000
		var isConsoleLogEnabled = true
		var crashlyticsInstance: FirebaseCrashlytics? = null
		var defaultInstance = Logcat("")
	}
	
	interface Interceptor {
		fun onDebug(tag: String, message: String)
		fun onInfo(tag: String, message: String)
		fun onWarning(tag: String, throwable: Throwable?)
		fun onWarning(tag: String, throwable: Throwable?, message: String?)
		fun onError(tag: String, throwable: Throwable, message: String?)
	}
	
	@Suppress("MemberVisibilityCanBePrivate")
	var interceptor: Interceptor? = null
	
	/***
	 * If Logcat.isConsoleLogEnabled is true
	 * then will print arguments to logcat console with Log.d
	 * This method do not add message to crashlytics
	 */
	fun debug(vararg x: Any?) {
		if (isConsoleLogEnabled) {
			sliceLog(buildMessage(*x)) { Log.d(tag, it) }
		}
		interceptor?.onDebug(tag, buildMessage(*x))
	}
	
	/***
	 * If Logcat.isConsoleLogEnabled is true
	 * then will print arguments to logcat console with Log.i
	 * Otherwise will add arguments to crashlytics if it provided
	 */
	fun info(vararg x: Any?) {
		val message = buildMessage(*x)
		if (isConsoleLogEnabled) {
			sliceLog(message) { Log.i(tag, it) }
		} else crashlyticsInstance?.log("$tag INFO: $message")
		interceptor?.onInfo(tag, message)
	}
	
	/***
	 * If Logcat.isConsoleLogEnabled is true
	 * then will print arguments to logcat console with Log.w
	 * Otherwise will add arguments to crashlytics if it provided
	 */
	fun warning(vararg x: Any?) {
		val message = buildMessage(*x)
		if (isConsoleLogEnabled) {
			sliceLog(message) { Log.w(tag, it) }
		} else crashlyticsInstance?.log("$tag WARNING: $message")
		interceptor?.onWarning(tag, null, message)
	}
	
	/***
	 * If Logcat.isConsoleLogEnabled is true
	 * then will print throwable to logcat console with Log.w
	 * Otherwise will add throwable`s stack trace to crashlytics if it provided
	 */
	fun warning(throwable: Throwable) {
		if (isConsoleLogEnabled) {
			Log.w(tag, "", throwable)
		} else crashlyticsInstance?.log("$tag WARNING: ${throwable.stackTraceToString()}")
		interceptor?.onWarning(tag, throwable, null)
	}
	
	/***
	 * If Logcat.isConsoleLogEnabled is true
	 * then will print throwable and message to logcat console with Log.w
	 * Otherwise will add throwable`s stack trace and message to crashlytics if it provided
	 */
	fun warning(throwable: Throwable, message: String) {
		if (isConsoleLogEnabled) {
			Log.w(tag, message, throwable)
		} else crashlyticsInstance?.log("$tag WARNING: $message")
		interceptor?.onWarning(tag, throwable, message)
	}
	
	/***
	 * If Logcat.isConsoleLogEnabled is true
	 * then will print throwable to logcat console with Log.e
	 * Otherwise will record exception with crashlytics if it provided
	 * It will create new record on Crashlytics Dashboard
	 */
	fun error(throwable: Throwable) {
		if (isConsoleLogEnabled) {
			Log.e(tag, "", throwable)
		} else crashlyticsInstance?.apply {
			crashlyticsInstance?.recordException(throwable)
			crashlyticsInstance?.sendUnsentReports()
		}
		interceptor?.onError(tag, throwable, null)
	}
	
	/***
	 * If Logcat.isConsoleLogEnabled is true
	 * then will print throwable and message to logcat console with Log.e
	 * Otherwise will record exception with crashlytics if it provided
	 * It will create new record on Crashlytics Dashboard
	 */
	fun error(throwable: Throwable, message: String) {
		if (isConsoleLogEnabled) {
			Log.e(tag, message, throwable)
		} else crashlyticsInstance?.apply {
			crashlyticsInstance?.log("$tag ERROR: $message")
			crashlyticsInstance?.recordException(throwable)
			crashlyticsInstance?.sendUnsentReports()
		}
		interceptor?.onError(tag, throwable, message)
	}
	
	private fun buildMessage(vararg x: Any?) =
		x.joinToString(separator = "; ") { it?.toString() ?: "null" }
	
	private inline fun sliceLog(message: String, call: (String) -> Unit) {
		if (message.length < LOG_MAX_LENGTH) {
			call(message)
			return
		}
		
		var loggedSymbols = 0
		while (loggedSymbols < message.length) {
			val take = message.substring(
				loggedSymbols,
				min(loggedSymbols + LOG_MAX_LENGTH, message.length)
			)
			call(take)
			loggedSymbols += take.length
		}
	}
	
}

fun log(vararg x: Any?) = Logcat.defaultInstance.debug(*x)

fun <T> Collection<T>.logAll() {
	this.forEach {
		log(it)
	}
}

fun <T> List<T>.logAll(call: (T) -> String?) {
	this.forEach {
		log(call(it))
	}
}

fun <K, V> Map<K, V>.logAll(call: (Map.Entry<K, V>) -> String?) {
	this.forEach {
		log(call(it))
	}
}