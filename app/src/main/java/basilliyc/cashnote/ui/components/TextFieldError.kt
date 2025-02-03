package basilliyc.cashnote.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import basilliyc.cashnote.R

enum class TextFieldError(@StringRes val res: Int) {
	ShouldNotBeEmpty(R.string.text_field_error_should_not_be_empty),
	IncorrectValue(R.string.text_field_error_incorrect_value),
	DeviationCantBeZero(R.string.transaction_form_toast_deviation_zero)
}

@Composable
fun TextFieldError(error: TextFieldError?, textAlign: TextAlign = TextAlign.Start) {
	if (error == null) return
	Text(
		modifier = Modifier.fillMaxWidth(),
		text = stringResource(id = error.res),
		textAlign = textAlign,
	)
}

