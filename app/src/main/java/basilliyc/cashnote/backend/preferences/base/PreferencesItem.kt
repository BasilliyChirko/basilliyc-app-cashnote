package basilliyc.cashnote.backend.preferences.base

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.KProperty

class PreferencesItem<T : Any>(
	val preferences: SharedPreferences,
	val key: String,
	val defaultValue: T,
	val onWrite: SharedPreferences.Editor.(value: T) -> Unit,
	val onRead: SharedPreferences.() -> T,
	val onRemove: SharedPreferences.Editor.() -> Unit = { remove(key) },
) {
	
	private var valueField: T? = null
	
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
		valueField = value
		
		preferences.edit().apply {
			onWrite(value)
		}.apply()
		
		mutableStateFlow.tryEmit(value)
	}
	
	fun remove() {
		valueField = null
		preferences.edit().apply { onRemove() }.apply()
		mutableStateFlow.tryEmit(defaultValue)
	}
	
	fun get(): T {
		valueField?.let { return it }
		
		if (!preferences.contains(key)) {
			return defaultValue.also { valueField = it }
		}
		
		return preferences.onRead().also { valueField = it }
	}
	
	inline fun update(transform: (T) -> T) {
		set(transform(get()))
	}
	
	private val mutableStateFlow by lazy { MutableStateFlow(get()) }
	val flow by lazy { mutableStateFlow.asStateFlow() }
	
	@Composable
	fun collectValue() = flow.collectAsState().value
	
	
}