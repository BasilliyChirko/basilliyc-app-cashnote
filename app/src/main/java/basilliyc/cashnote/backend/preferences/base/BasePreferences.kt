package basilliyc.cashnote.backend.preferences.base

import android.content.Context
import android.content.SharedPreferences
import basilliyc.cashnote.utils.inject
import kotlin.collections.first

abstract class BasePreferences(
	name: String? = null,
) {
	
	protected val prefs: SharedPreferences by lazy {
		inject<Context>().value.getSharedPreferences(
			name ?: this::class.java.simpleName,
			Context.MODE_PRIVATE
		)
	}
	
	protected fun boolean(
		key: String,
		defaultValue: Boolean,
	) = PreferencesItem<Boolean>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putBoolean(key, it) },
		onRead = { getBoolean(key, false) },
	)
	
	protected fun booleanOrNull(
		key: String,
		defaultValue: Boolean? = null,
	) = PreferencesItemNullable<Boolean>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putBoolean(key, it) },
		onRead = { getBoolean(key, false) },
	)
	
	protected fun int(
		key: String,
		defaultValue: Int,
	) = PreferencesItem<Int>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putInt(key, it) },
		onRead = { getInt(key, 0) },
	)
	
	protected fun intOrNull(
		key: String,
		defaultValue: Int? = null,
	) = PreferencesItemNullable<Int>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putInt(key, it) },
		onRead = { getInt(key, 0) },
	)
	
	protected fun long(
		key: String,
		defaultValue: Long,
	) = PreferencesItem<Long>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putLong(key, it) },
		onRead = { getLong(key, 0L) },
	)
	
	protected fun longOrNull(
		key: String,
		defaultValue: Long? = null,
	) = PreferencesItemNullable<Long>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putLong(key, it) },
		onRead = { getLong(key, 0L) },
	)
	
	protected fun float(
		key: String,
		defaultValue: Float,
	) = PreferencesItem<Float>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putFloat(key, it) },
		onRead = { getFloat(key, 0F) },
	)
	
	protected fun floatOrNull(
		key: String,
		defaultValue: Float? = null,
	) = PreferencesItemNullable<Float>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putFloat(key, it) },
		onRead = { getFloat(key, 0F) },
	)
	
	protected fun double(
		key: String,
		defaultValue: Double,
	) = custom<Double>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.toString() },
		convertFromString = { it.toDouble() }
	)
	
	protected fun doubleOrNull(
		key: String,
		defaultValue: Double? = null,
	) = customOrNull<Double>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.toString() },
		convertFromString = { it.toDouble() }
	)
	
	protected inline fun <reified T : Any> custom(
		key: String,
		defaultValue: T,
		crossinline convertToString: (value: T) -> String,
		crossinline convertFromString: (string: String) -> T,
	) = PreferencesItem<T>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putString(key, convertToString(it)) },
		onRead = { convertFromString(getString(key, "")!!) }
	)
	
	protected inline fun <reified T : Any> customOrNull(
		key: String,
		defaultValue: T? = null,
		crossinline convertToString: (value: T) -> String,
		crossinline convertFromString: (string: String) -> T,
	) = PreferencesItemNullable<T>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putString(key, convertToString(it)) },
		onRead = { convertFromString(getString(key, "")!!) }
	)
	
	protected inline fun <reified T : Enum<T>> enum(
		key: String,
		defaultValue: T,
	): PreferencesItem<T> {
		val values = T::class.java.enumConstants!!.map { it as Enum<T> }
		T::class.java.isEnum || throw IllegalArgumentException("T must be an enum")
		return custom<T>(
			key = key,
			defaultValue = defaultValue,
			convertToString = { (it as Enum<*>).name },
			convertFromString = { name ->
				values.first { it.name == name } as T
			}
		)
	}
	
	protected inline fun <reified T : Enum<T>> enumOrNull(
		key: String,
		defaultValue: T? = null,
	): PreferencesItemNullable<T> {
		val values = T::class.java.enumConstants!!.map { it as Enum<T> }
		T::class.java.isEnum || throw IllegalArgumentException("T must be an enum")
		return customOrNull<T>(
			key = key,
			defaultValue = defaultValue,
			convertToString = { (it as Enum<*>).name },
			convertFromString = { name ->
				values.first { it.name == name } as T
			}
		)
	}
	
}