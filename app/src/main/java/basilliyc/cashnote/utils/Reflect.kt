package basilliyc.cashnote.utils

import androidx.annotation.Keep
import java.lang.reflect.ParameterizedType
import java.lang.reflect.TypeVariable

@Suppress("UNCHECKED_CAST")
@Keep
fun <T> Any.getGenericSuperClass(index: Int = 0): Class<T> {
	val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[index]
	if (type is Class<*>) return type as Class<T>
	if (type is TypeVariable<*>) throw ClassCastException("Can`t cast $type to Class type. Probably you should try wrap your class with inline function with reified params")
	throw ClassCastException("Can`t cast $type to Class type")
}