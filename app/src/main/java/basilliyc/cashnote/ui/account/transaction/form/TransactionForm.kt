package basilliyc.cashnote.ui.account.transaction.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import basilliyc.cashnote.ui.theme.onSurfaceVariantDay
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.rememberSingleRunner
import basilliyc.cashnote.utils.toPriceString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

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
		onDeviationChanged = viewModel::onDeviationChanged,
		onBalanceChanged = viewModel::onBalanceChanged,
		onFocusChanged = viewModel::onFocusChanged,
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
			deviationTextState = TextFieldState("500.00"),
			balanceTextState = TextFieldState("500.00"),
		),
		onDeviationChanged = {},
		onBalanceChanged = {},
		onFocusChanged = {},
	)
}


//--------------------------------------------------------------------------------------------------
//  PAGE
//--------------------------------------------------------------------------------------------------

@Composable
private fun Page(
	state: TransactionFormState,
	onBalanceChanged: (String) -> Unit,
	onDeviationChanged: (String) -> Unit,
	onFocusChanged: (TransactionFormState.Focus) -> Unit,
) {
	when (val page = state.page) {
		is TransactionFormState.Page.Data -> PageData(
			page = page,
			onBalanceChanged = onBalanceChanged,
			onDeviationChanged = onDeviationChanged,
			onFocusChanged = onFocusChanged,
		)
		
		TransactionFormState.Page.Loading -> PageLoading()
	}
}

@Composable
private fun PageData(
	page: TransactionFormState.Page.Data,
	onBalanceChanged: (String) -> Unit,
	onDeviationChanged: (String) -> Unit,
	onFocusChanged: (TransactionFormState.Focus) -> Unit,
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
				PageDataInputDeviation(
					page = page,
					onDeviationChanged = onDeviationChanged,
					onFocusChanged = onFocusChanged,
				)
				
				Spacer(modifier = Modifier.height(32.dp))
				PageDataInputBalance(
					page = page,
					onBalanceChanged = onBalanceChanged,
					onFocusChanged = onFocusChanged,
				)
			}
		}
	)
}


@Composable
private fun ColumnScope.PageDataInputDeviation(
	page: TransactionFormState.Page.Data,
	onDeviationChanged: (String) -> Unit,
	onFocusChanged: (TransactionFormState.Focus) -> Unit,
) {
	if (page.focusedField != TransactionFormState.Focus.Deviation) {
		
		Column(
			modifier = Modifier.clickable(onClick = { onFocusChanged(TransactionFormState.Focus.Deviation) }),
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
		
	} else {
		
		ValueTextField(
			state = page.deviationTextState,
			focusedFieldCurrent = page.focusedField,
			focusedFieldTarget = TransactionFormState.Focus.Deviation,
			onValueChange = onDeviationChanged,
			onFocusChanged = onFocusChanged,
		)
		
	}
}

@Composable
private fun ColumnScope.PageDataInputBalance(
	page: TransactionFormState.Page.Data,
	onBalanceChanged: (String) -> Unit,
	onFocusChanged: (TransactionFormState.Focus) -> Unit,
) {
	if (page.focusedField != TransactionFormState.Focus.Balance) {
		
		Column(
			modifier = Modifier.clickable(onClick = { onFocusChanged(TransactionFormState.Focus.Balance) }),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(text = stringResource(R.string.transaction_form_new_balance))
			Text(text = (page.balanceWithoutDeviation + page.deviation).toPriceString(false))
		}
		
	} else {
		
		ValueTextField(
			state = page.balanceTextState,
			focusedFieldCurrent = page.focusedField,
			focusedFieldTarget = TransactionFormState.Focus.Balance,
			onValueChange = onBalanceChanged,
			onFocusChanged = onFocusChanged,
		)
		
	}
}

@Composable
private fun ColumnScope.ValueTextField(
	state: TextFieldState,
	focusedFieldCurrent: TransactionFormState.Focus,
	focusedFieldTarget: TransactionFormState.Focus,
	onValueChange: (String) -> Unit,
	onFocusChanged: (TransactionFormState.Focus) -> Unit,
) {
	var selectionRange by remember { mutableStateOf(TextRange(state.value.length)) }
	
	val focusRequester = remember { FocusRequester() }
	val focusTrigger = remember { MutableStateFlow(focusedFieldCurrent) }
	val scope = rememberCoroutineScope()
	LaunchedEffect(scope) {
		focusTrigger
			.filter { it == focusedFieldTarget }
			.collectLatest {
				focusRequester.requestFocus()
				selectionRange = TextRange(state.value.length)
			}
	}
	
	val textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center)
	
	TextField(
		modifier = Modifier
			.fillMaxWidth()
			.focusRequester(focusRequester)
			.onFocusChanged {
				if (it.isFocused) onFocusChanged(focusedFieldTarget)
			},
		value = TextFieldValue(
			text = state.value,
			selection = selectionRange
		),
		onValueChange = {
			selectionRange = it.selection
			onValueChange(it.text)
		},
		placeholder = {
			Text(
				modifier = Modifier.fillMaxWidth(),
				text = "0",
				style = textStyle,
				textAlign = TextAlign.Center,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		},
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
		textStyle = textStyle,
		colors = TextFieldDefaults.colors(
			errorContainerColor = Color.Transparent,
			focusedContainerColor = Color.Transparent,
			disabledContainerColor = Color.Transparent,
			unfocusedContainerColor = Color.Transparent,
			focusedIndicatorColor = Color.Transparent,
			unfocusedIndicatorColor = Color.Transparent,
			disabledIndicatorColor = Color.Transparent,
			errorIndicatorColor = Color.Transparent,
			cursorColor = Color.Transparent,
			errorCursorColor = Color.Transparent,
		),
	)
}
