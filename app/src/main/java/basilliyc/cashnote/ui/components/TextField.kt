package basilliyc.cashnote.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

data class TextFieldState(
	val value: String,
	val error: TextFieldError? = null,
)

fun TextFieldState.setError(error: TextFieldError?): TextFieldState {
	return copy(error = error)
}

val TextFieldDefaults.modifier
	get() = Modifier
		.fillMaxWidth()
		.padding(
			start = 16.dp,
			end = 16.dp,
			top = 8.dp,
		)

@Composable
fun TextField(
	state: TextFieldState,
	onValueChange: (String) -> Unit,
	modifier: Modifier = TextFieldDefaults.modifier,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	textStyle: TextStyle = LocalTextStyle.current,
	label: @Composable (() -> Unit)? = null,
	placeholder: @Composable (() -> Unit)? = null,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	prefix: @Composable (() -> Unit)? = null,
	suffix: @Composable (() -> Unit)? = null,
	supportingText: @Composable (() -> Unit)? = null,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	singleLine: Boolean = false,
	maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
	minLines: Int = 1,
	interactionSource: MutableInteractionSource? = null,
	shape: Shape = TextFieldDefaults.shape,
	colors: TextFieldColors = TextFieldDefaults.colors(),
) {
	androidx.compose.material3.TextField(
		value = state.value,
		onValueChange = onValueChange,
		modifier = modifier,
		enabled = enabled,
		readOnly = readOnly,
		textStyle = textStyle,
		label = label,
		placeholder = placeholder,
		leadingIcon = leadingIcon,
		trailingIcon = trailingIcon,
		prefix = prefix,
		suffix = suffix,
		supportingText = supportingText ?: { TextFieldError(state.error) },
		isError = state.error != null,
		visualTransformation = visualTransformation,
		keyboardOptions = keyboardOptions,
		keyboardActions = keyboardActions,
		singleLine = singleLine,
		maxLines = maxLines,
		minLines = minLines,
		interactionSource = interactionSource,
		shape = shape,
		colors = colors,
	)
}