package basilliyc.cashnote.utils

import android.content.Context
import org.koin.core.qualifier.Qualifier
import org.koin.java.KoinJavaComponent

@JvmOverloads
inline fun <reified T> inject(
	qualifier: Qualifier? = null,
): Lazy<T> {
	return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
		KoinJavaComponent.get(T::class.java, qualifier, null)
	}
}

@JvmOverloads
inline fun <reified I, reified O> inject(
	qualifier: Qualifier? = null,
	crossinline callback: I.() -> O,
): Lazy<O> {
	return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
		callback(KoinJavaComponent.get(I::class.java, qualifier, null))
	}
}

inline fun <reified O> injectSystemService(
	qualifier: Qualifier? = null,
): Lazy<O> {
	return inject<Context, O>(qualifier) {
		getSystemService(O::class.java)
	}
}