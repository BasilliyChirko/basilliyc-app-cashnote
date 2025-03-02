package basilliyc.cashnote.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.applyIf(
	condition: Modifier.() -> Boolean,
	block: @Composable Modifier.() -> Modifier
): Modifier {
	if (condition()) {
		return block()
	}
	return this
}
@Composable
fun Modifier.applyIf(
	condition: Boolean,
	block: @Composable Modifier.() -> Modifier
): Modifier {
	if (condition) {
		return block()
	}
	return this
}