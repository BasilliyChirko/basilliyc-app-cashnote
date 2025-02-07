package basilliyc.cashnote.backend.preferences.base

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.KProperty

class PreferencesItem<T : Any>(
	val preferences: SharedPreferences,
	val key: String,
	val defaultValue: T,
	val onWrite: SharedPreferences.Editor.(value: T) -> Unit,
	val onRead: SharedPreferences.() -> T,
) {
	
	var value: T
		get() = get()
		set(value) = set(value)
	
	@Suppress("NOTHING_TO_INLINE")
	inline operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value
	
	@Suppress("NOTHING_TO_INLINE")
	inline operator fun setValue(
		thisObj: Any?,
		property: KProperty<*>,
		value: T,
	) {
		this.value = value
	}
	
	fun set(value: T) {
		
		preferences.edit().apply {
			onWrite(value)
		}.apply()
		
		mutableStateFlow.tryEmit(value)
	}
	
	fun remove() {
		preferences.edit().remove(key).apply()
		mutableStateFlow.tryEmit(defaultValue)
	}
	
	fun get(): T {
		if (!preferences.contains(key)) {
			return defaultValue
		}
		
		return preferences.onRead()
	}
	
	private val mutableStateFlow by lazy { MutableStateFlow(get()) }
	val flow by lazy { mutableStateFlow.asStateFlow() }
	
}