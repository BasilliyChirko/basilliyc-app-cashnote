package basilliyc.cashnote.utils

inline fun <T> Iterable<T>.updateIf(condition: (T) -> Boolean, transform: (T) -> T): List<T> {
	return map {
		if (condition(it)) transform(it)
		else it
	}
}