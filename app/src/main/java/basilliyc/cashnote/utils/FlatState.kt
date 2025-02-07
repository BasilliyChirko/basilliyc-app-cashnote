package basilliyc.cashnote.utils

import androidx.compose.runtime.MutableState
import kotlin.reflect.KProperty

open class FlatState<T>(override var value: T) : MutableState<T> {
	override fun component1(): T {
		return value
	}
	
	override fun component2(): (T) -> Unit {
		return { value = it }
	}
	
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
}