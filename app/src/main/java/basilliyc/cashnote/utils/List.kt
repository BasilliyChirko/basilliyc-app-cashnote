package basilliyc.cashnote.utils

inline fun <T> Iterable<T>.updateIf(condition: (T) -> Boolean, transform: (T) -> T): List<T> {
	return map {
		if (condition(it)) transform(it)
		else it
	}
}

inline fun <T, Key, Value> Iterable<T>.toMap(transform: (T) -> Pair<Key, Value>): Map<Key, Value> {
	return map(transform).toMap()
}

