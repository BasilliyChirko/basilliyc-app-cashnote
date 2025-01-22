package basilliyc.cashnote.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.R

enum class TextFieldError(@StringRes val res: Int) {
	ShouldNotBeEmpty(R.string.text_field_error_should_not_be_empty),
	IncorrectValue(R.string.text_field_error_incorrect_value),
}

@Composable
fun TextFieldError(error: TextFieldError?) {
	if (error == null) return
	Text(text = stringResource(id = error.res))
}

