@file:Suppress("SameParameterValue", "unused")

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
	
	protected val fields = ArrayList<PreferencesItem<*>>()
	protected val fieldsNullable = ArrayList<PreferencesItemNullable<*>>()
	
	fun clear() {
		fields.forEach { it.remove() }
		fieldsNullable.forEach { it.remove() }
		prefs.edit().clear().apply()
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
	).also { fields.add(it) }
	
	protected fun booleanOrNull(
		key: String,
		defaultValue: Boolean? = null,
	) = PreferencesItemNullable<Boolean>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putBoolean(key, it) },
		onRead = { getBoolean(key, false) },
	).also { fieldsNullable.add(it) }
	
	protected fun int(
		key: String,
		defaultValue: Int,
	) = PreferencesItem<Int>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putInt(key, it) },
		onRead = { getInt(key, 0) },
	).also { fields.add(it) }
	
	protected fun intOrNull(
		key: String,
		defaultValue: Int? = null,
	) = PreferencesItemNullable<Int>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putInt(key, it) },
		onRead = { getInt(key, 0) },
	).also { fieldsNullable.add(it) }
	
	protected fun long(
		key: String,
		defaultValue: Long,
	) = PreferencesItem<Long>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putLong(key, it) },
		onRead = { getLong(key, 0L) },
	).also { fields.add(it) }
	
	protected fun longOrNull(
		key: String,
		defaultValue: Long? = null,
	) = PreferencesItemNullable<Long>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putLong(key, it) },
		onRead = { getLong(key, 0L) },
	).also { fieldsNullable.add(it) }
	
	protected fun float(
		key: String,
		defaultValue: Float,
	) = PreferencesItem<Float>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putFloat(key, it) },
		onRead = { getFloat(key, 0F) },
	).also { fields.add(it) }
	
	protected fun floatOrNull(
		key: String,
		defaultValue: Float? = null,
	) = PreferencesItemNullable<Float>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putFloat(key, it) },
		onRead = { getFloat(key, 0F) },
	).also { fieldsNullable.add(it) }
	
	protected fun double(
		key: String,
		defaultValue: Double,
	) = custom<Double>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.toString() },
		convertFromString = { it.toDouble() }
	).also { fields.add(it) }
	
	protected fun doubleOrNull(
		key: String,
		defaultValue: Double? = null,
	) = customOrNull<Double>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.toString() },
		convertFromString = { it.toDouble() }
	).also { fieldsNullable.add(it) }
	
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
	).also { fields.add(it) }
	
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
	).also { fieldsNullable.add(it) }
	
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
		).also { fields.add(it) }
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
		).also { fieldsNullable.add(it) }
	}
	
	protected fun string(
		key: String,
		defaultValue: String,
	) = PreferencesItem<String>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putString(key, it) },
		onRead = { getString(key, "")!! }
	).also { fields.add(it) }
	
	protected fun stringOrNull(
		key: String,
		defaultValue: String? = null,
	) = PreferencesItemNullable<String>(
		preferences = prefs,
		key = key,
		defaultValue = defaultValue,
		onWrite = { putString(key, it) },
		onRead = { getString(key, "")!! }
	).also { fieldsNullable.add(it) }
	
	protected fun intList(
		key: String,
		defaultValue: List<Int>,
	) = custom<List<Int>>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.joinToString(",") },
		convertFromString = {
			if (it.isEmpty()) return@custom emptyList()
			it.split(",").map { it.toInt() }
		}
	).also { fields.add(it) }
	
	protected fun intListOrNull(
		key: String,
		defaultValue: List<Int>? = null,
	) = customOrNull<List<Int>>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.joinToString(",") },
		convertFromString = {
			if (it.isEmpty()) return@customOrNull emptyList()
			it.split(",").map { it.toInt() }
		}
	).also { fieldsNullable.add(it) }
	
	protected fun longList(
		key: String,
		defaultValue: List<Long>,
	) = custom<List<Long>>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.joinToString(",") },
		convertFromString = {
			if (it.isEmpty()) return@custom emptyList()
			it.split(",").map { it.toLong() }
		}
	).also { fields.add(it) }
	
	protected fun longListOrNull(
		key: String,
		defaultValue: List<Long>? = null,
	) = customOrNull<List<Long>>(
		key = key,
		defaultValue = defaultValue,
		convertToString = { it.joinToString(",") },
		convertFromString = {
			if (it.isEmpty()) return@customOrNull emptyList()
			it.split(",").map { it.toLong() }
		}
	).also { fieldsNullable.add(it) }
	
	
}