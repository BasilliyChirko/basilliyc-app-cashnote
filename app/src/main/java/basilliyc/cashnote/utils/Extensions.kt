package basilliyc.cashnote.utils

inline fun <T> takeIf(condition: Boolean, block: () -> T): T? {
	return if (condition) block() else null
}

inline fun <T> T.letIf(condition: (T) -> Boolean, block: (T) -> T): T {
	return if (condition(this)) block(this) else this
}

inline fun <T> T.applyIf(condition: T.() -> Boolean, block: T.() -> T): T {
	return if (condition(this)) block(this) else this
}