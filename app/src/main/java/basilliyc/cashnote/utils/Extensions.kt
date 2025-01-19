package basilliyc.cashnote.utils

inline fun <T> takeIf(condition: Boolean, block: () -> T): T? {
	return if (condition) block() else null
}