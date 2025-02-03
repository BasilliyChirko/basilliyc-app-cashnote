package basilliyc.cashnote.ui.account.transaction.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.components.modifier
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.rememberSingleRunner
import basilliyc.cashnote.utils.toPriceString
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun TransactionForm() {
	val viewModel = viewModel<TransactionFormViewModel>()
	val navController = LocalNavController.current
	val singleRunner = rememberSingleRunner()
	val state = viewModel.state
	
	Page(
		state = state,
		onInputTypeChanged = viewModel::onInputTypeChanged,
		onInputChanged = viewModel::onInputChanged,
	)
}

@Composable
@Preview(showBackground = true)
fun TransactionFormPreview() = DefaultPreview {
	PageData(
		page = TransactionFormState.Page.Data(
			account = PreviewValues.accountFilled,
			category = PreviewValues.categoryHome,
			isNew = true,
			isInputDeviation = true,
			timeInMillis = System.currentTimeMillis(),
			balanceWithoutDeviation = 0.0,
			comment = TextFieldState(""),
			deviation = 0.0,
			input = TextFieldState("500.00")
		),
		onInputTypeChanged = {},
		onInputChanged = {},
	)
}


//--------------------------------------------------------------------------------------------------
//  PAGE
//--------------------------------------------------------------------------------------------------

@Composable
private fun Page(
	state: TransactionFormState,
	onInputTypeChanged: (Boolean) -> Unit,
	onInputChanged: (String) -> Unit,
) {
	when (val page = state.page) {
		is TransactionFormState.Page.Data -> PageData(
			page = page,
			onInputTypeChanged = onInputTypeChanged,
			onInputChanged = onInputChanged,
		)
		
		TransactionFormState.Page.Loading -> PageLoading()
	}
}

@Composable
private fun PageData(
	page: TransactionFormState.Page.Data,
	onInputTypeChanged: (Boolean) -> Unit,
	onInputChanged: (String) -> Unit,
) {
	ScaffoldBox(
		topBar = { SimpleActionBar(title = page.category.name) },
		content = {
			Column(
				modifier = Modifier.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Spacer(modifier = Modifier.height(32.dp))
				Spacer(modifier = Modifier.height(32.dp))
				
				Spacer(modifier = Modifier.height(32.dp))
				PageDataInput(
					page = page,
					onInputChanged = onInputChanged,
				)
				
				Spacer(modifier = Modifier.height(32.dp))
				PageDataSecondaryText(
					page = page,
					onInputTypeChanged = onInputTypeChanged,
				)
			}
		}
	)
}


@Composable
private fun ColumnScope.PageDataInput(
	page: TransactionFormState.Page.Data,
	onInputChanged: (String) -> Unit,
) {
	
	val focusRequester = remember { FocusRequester() }
	LaunchedEffect(focusRequester) {
		focusRequester.requestFocus()
	}
	
	var selectionRange by remember { mutableStateOf(TextRange(page.input.value.length)) }
	
	LaunchedEffect(page.isInputDeviation) {
		selectionRange = TextRange(page.input.value.length)
	}
	
	TextField(
		modifier = TextFieldDefaults.modifier
			.focusRequester(focusRequester),
		value = TextFieldValue(
			text = page.input.value,
			selection = selectionRange
		),
		onValueChange = {
			selectionRange = it.selection
			onInputChanged(it.text)
		},
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
		textStyle = MaterialTheme.typography.displayMedium.copy(
			textAlign = TextAlign.Center,
		),
		colors = TextFieldDefaults.colors(
			errorContainerColor = Color.Transparent,
			focusedContainerColor = Color.Transparent,
			disabledContainerColor = Color.Transparent,
			unfocusedContainerColor = Color.Transparent,
			focusedIndicatorColor = Color.Transparent,
			unfocusedIndicatorColor = Color.Transparent,
			disabledIndicatorColor = Color.Transparent,
			errorIndicatorColor = Color.Transparent,
		),
	)
	
//	TextField(
//		modifier = TextFieldDefaults.modifier
//			.focusRequester(focusRequester),
//		state = page.input,
//		onValueChange = onInputChanged,
//		singleLine = true,
//		keyboardOptions = KeyboardOptions(
//			keyboardType = KeyboardType.Number
//		),
//		textStyle = MaterialTheme.typography.displayMedium.copy(
//			textAlign = TextAlign.Center,
//		),
//		colors = TextFieldDefaults.colors(
//			errorContainerColor = Color.Transparent,
//			focusedContainerColor = Color.Transparent,
//			disabledContainerColor = Color.Transparent,
//			unfocusedContainerColor = Color.Transparent,
//			focusedIndicatorColor = Color.Transparent,
//			unfocusedIndicatorColor = Color.Transparent,
//			disabledIndicatorColor = Color.Transparent,
//			errorIndicatorColor = Color.Transparent,
//		),
//	)
}

@Composable
private fun ColumnScope.PageDataSecondaryText(
	page: TransactionFormState.Page.Data,
	onInputTypeChanged: (isInputDeviation: Boolean) -> Unit,
) {
	
	if (page.isInputDeviation) {
		Column(
			modifier = Modifier.clickable(onClick = { onInputTypeChanged(false) }),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(text = stringResource(R.string.transaction_form_new_balance))
			Text(text = (page.balanceWithoutDeviation + page.deviation).toPriceString(false))
		}
	} else {
		Column(
			modifier = Modifier.clickable(onClick = { onInputTypeChanged(true) }),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(
				text = stringResource(
					when {
						page.deviation > 0 -> R.string.transaction_form_label_type_increase
						page.deviation < 0 -> R.string.transaction_form_label_type_decrease
						else -> R.string.transaction_form_label_type_no_changes
					}
				)
			)
			Text(text = (page.deviation).toPriceString(true))
		}
	}
	
	
}
