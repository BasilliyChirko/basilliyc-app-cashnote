@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.transaction.form

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.SimpleDatePickerDialog
import basilliyc.cashnote.ui.components.SimpleTimePickerDialog
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.components.rememberPopupMenuState
import basilliyc.cashnote.ui.symbol
import basilliyc.cashnote.ui.theme.backgroundCardGradient
import basilliyc.cashnote.ui.theme.backgroundPageGradient
import basilliyc.cashnote.ui.theme.colorGrey99
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.TimestampStyle
import basilliyc.cashnote.utils.Vibration
import basilliyc.cashnote.utils.format
import basilliyc.cashnote.utils.rememberSingleRunner
import basilliyc.cashnote.utils.rememberVibrator
import basilliyc.cashnote.utils.showToast
import basilliyc.cashnote.utils.toPriceString
import basilliyc.cashnote.utils.vibrate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun TransactionForm() {
	val viewModel = viewModel<TransactionFormViewModel>()
	Page(page = viewModel.state.page, listener = viewModel)
	Dialog(dialog = viewModel.state.dialog, listener = viewModel)
	Action(action = viewModel.state.action, listener = viewModel)
}

//--------------------------------------------------------------------------------------------------
//  ACTION
//--------------------------------------------------------------------------------------------------

@Composable
private fun Action(
	action: TransactionFormState.Action?,
	listener: TransactionFormListener,
) {
	
	val context = LocalContext.current
	val navController = LocalNavController.current
	val singleRunner = rememberSingleRunner()
	
	LaunchedEffect(action) {
		when (action) {
			null -> Unit
			
			TransactionFormState.Action.SaveSuccess -> {
				navController.popBackStack()
			}
			
			TransactionFormState.Action.SaveError -> {
				context.showToast(R.string.transaction_form_toast_save_error)
			}
			
			TransactionFormState.Action.DeviationCantBeZero -> {
				context.showToast(R.string.transaction_form_toast_deviation_zero)
			}
		}
		listener.onActionConsumed()
	}
	
}

//--------------------------------------------------------------------------------------------------
//  PAGE
//--------------------------------------------------------------------------------------------------

@Composable
@Preview(showBackground = true)
fun PagePreview() = DefaultPreview {
	Page(
		page = TransactionFormState.Page.Data(
			account = PreviewValues.accountTestUSD,
			category = PreviewValues.categoryHome,
			categoryOriginal = PreviewValues.categoryHome,
			isNew = true,
			isInputDeviation = true,
			timeInMillis = System.currentTimeMillis(),
			balanceWithoutDeviation = 0.0,
			comment = TextFieldState(""),
			deviation = 500.0,
			deviationTextState = TextFieldState("500.00"),
			balanceTextState = TextFieldState("500.00"),
			deviationTextPlaceholder = "0",
			balanceTextPlaceholder = "0",
			availableCategories = PreviewValues.categories,
		),
		listener = object : TransactionFormListener {}
	)
}

@Composable
private fun Page(
	page: TransactionFormState.Page,
	listener: TransactionFormListener,
) = when (page) {
	is TransactionFormState.Page.Data -> PageData(page = page, listener = listener)
	is TransactionFormState.Page.Loading -> PageLoading()
}

@Composable
private fun PageData(
	page: TransactionFormState.Page.Data,
	listener: TransactionFormListener,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				modifier = Modifier.backgroundPageGradient(page.account.color),
				title = stringResource(R.string.transaction_form_account, page.account.name),
				actions = {
					IconButton(
						onClick = listener::onSaveClicked,
						imageVector = Icons.Filled.Done,
						contentDescription = stringResource(R.string.transaction_form_action_save)
					)
				},
				containerColor = Color.Transparent
			)
		},
		content = {
			Column(
				modifier = Modifier.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				
				Spacer(modifier = Modifier.height(48.dp))
				
				PageDataInputDeviation(page = page, listener = listener)
				PageDataInputBalance(page = page, listener = listener)
				
				Spacer(modifier = Modifier.height(48.dp))
				
				PageDataInputComment(page = page, listener = listener)
				PageDataOther(page = page, listener = listener)
			}
		}
	)
}

@Composable
private fun ColumnScope.PageDataOther(
	page: TransactionFormState.Page.Data,
	listener: TransactionFormListener,
) {
	
	val contentPadding = PaddingValues(
		top = ButtonDefaults.ContentPadding.calculateTopPadding(),
		bottom = ButtonDefaults.ContentPadding.calculateBottomPadding(),
		start = 8.dp,
		end = 8.dp,
	)
	
	Row(
		modifier = Modifier
			.padding(horizontal = 16.dp),
		horizontalArrangement = Arrangement.Center,
	) {
		OutlinedButton(
			modifier = Modifier
				.height(ButtonDefaults.MinHeight),
			onClick = listener::onDateClicked,
			content = {
				Text(
					modifier = Modifier.padding(horizontal = 8.dp),
					text = page.timeInMillis.format(TimestampStyle.YearMonthDay),
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodySmall,
				)
			},
			contentPadding = contentPadding,
		)
		Spacer(modifier = Modifier.width(16.dp))
		OutlinedButton(
			modifier = Modifier
				.height(ButtonDefaults.MinHeight),
			onClick = listener::onTimeClicked,
			content = {
				Text(
					modifier = Modifier.padding(horizontal = 8.dp),
					text = page.timeInMillis.format(TimestampStyle.HourMinute),
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodySmall,
				)
			},
			contentPadding = contentPadding,
		)
		Spacer(modifier = Modifier.width(16.dp))
		val popupMenuState = rememberPopupMenuState()
		PopupMenu(
			state = popupMenuState,
			anchor = {
				OutlinedButton(
					modifier = Modifier
						.backgroundCardGradient(
							color = page.category.color,
							shape = CircleShape,
						)
						.height(ButtonDefaults.MinHeight),
					onClick = { popupMenuState.expand() },
					content = {
						Row(
							modifier = Modifier.padding(horizontal = 8.dp),
							verticalAlignment = Alignment.CenterVertically,
						) {
							page.category.icon?.let {
								Icon(
									modifier = Modifier.padding(end = 8.dp),
									imageVector = it.imageVector,
									contentDescription = page.category.name
								)
							}
							Text(
								text = page.category.name,
								textAlign = TextAlign.Center,
								style = MaterialTheme.typography.bodySmall,
							)
						}
					},
					contentPadding = contentPadding,
				)
			},
			items = {
				page.availableCategories.forEach { category ->
					PopupMenuItem(
						onClick = {
							popupMenuState.collapse()
							listener.onCategoryChanged(category)
						},
						text = category.name,
						leadingIcon = category.icon?.imageVector
					)
				}
			}
		)
	}
}

@Composable
private fun ColumnScope.PageDataInputDeviation(
	page: TransactionFormState.Page.Data,
	listener: TransactionFormListener,
) {
	val vibrator = rememberVibrator()
	if (page.focusedField != TransactionFormState.Focus.Deviation) {
		
		Column(
			modifier = Modifier
				.fillMaxWidth()
//				.clickable(onClick = {
//					vibrator.vibrate(Vibration.Short)
//					listener.onFocusChanged(TransactionFormState.Focus.Deviation)
//				})
				.pointerInput(Unit) {
					detectTapGestures(
						onPress = {
							vibrator.vibrate(Vibration.Short)
							listener.onFocusChanged(TransactionFormState.Focus.Deviation)
						}
					)
				},
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				text = stringResource(
					when {
						page.deviation > 0 -> R.string.transaction_form_label_type_increase
						page.deviation < 0 -> R.string.transaction_form_label_type_decrease
						else -> R.string.transaction_form_label_type_no_changes
					}
				)
			)
			Text(text = buildString {
				append(page.account.currency.symbol)
				append(" ")
				append((page.deviation).toPriceString(true))
			})
			Spacer(modifier = Modifier.height(16.dp))
		}
		
	} else {
		
		ValueTextField(
			state = page.deviationTextState,
			focusedFieldCurrent = page.focusedField,
			focusedFieldTarget = TransactionFormState.Focus.Deviation,
			onValueChange = listener::onDeviationChanged,
			onFocusChanged = listener::onFocusChanged,
			onSaveClicked = listener::onSaveClicked,
			placeholder = page.deviationTextPlaceholder
		)
		
	}
}

@Composable
private fun ColumnScope.PageDataInputBalance(
	page: TransactionFormState.Page.Data,
	listener: TransactionFormListener,
) {
	val vibrator = rememberVibrator()
	if (page.focusedField != TransactionFormState.Focus.Balance) {
		
		Column(
			modifier = Modifier
				.fillMaxWidth()
//				.clickable(onClick = { listener.onFocusChanged(TransactionFormState.Focus.Balance) })
				.pointerInput(Unit) {
					detectTapGestures(
						onLongPress = {
							vibrator.vibrate(Vibration.Short)
							listener.onFocusChanged(TransactionFormState.Focus.Balance)
						}
					)
				},
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Spacer(modifier = Modifier.height(16.dp))
			Text(text = stringResource(R.string.transaction_form_new_balance))
			Text(text = buildString {
				append(page.account.currency.symbol)
				append(" ")
				append((page.balanceWithoutDeviation + page.deviation).toPriceString(false))
			})
			Spacer(modifier = Modifier.height(16.dp))
		}
		
	} else {
		
		ValueTextField(
			state = page.balanceTextState,
			focusedFieldCurrent = page.focusedField,
			focusedFieldTarget = TransactionFormState.Focus.Balance,
			onValueChange = listener::onBalanceChanged,
			onFocusChanged = listener::onFocusChanged,
			onSaveClicked = listener::onSaveClicked,
			placeholder = page.balanceTextPlaceholder
		)
		
	}
}

@Composable
private fun ColumnScope.PageDataInputComment(
	page: TransactionFormState.Page.Data,
	listener: TransactionFormListener,
) {
	var selectionRange by remember { mutableStateOf(TextRange(page.comment.value.length)) }
	
	val focusRequester = remember { FocusRequester() }
	val focusTrigger = remember { MutableStateFlow(page.focusedField) }
	val scope = rememberCoroutineScope()
	LaunchedEffect(scope) {
		focusTrigger
			.filter { it == TransactionFormState.Focus.Comment }
			.collectLatest {
				focusRequester.requestFocus()
				selectionRange = TextRange(page.comment.value.length)
			}
	}
	
	val isFocused = page.focusedField == TransactionFormState.Focus.Comment
	
	val textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
	
	TextField(
		modifier = Modifier
			.fillMaxWidth()
			.focusRequester(focusRequester)
			.onFocusChanged {
				if (it.isFocused) listener.onFocusChanged(TransactionFormState.Focus.Comment)
			},
		value = TextFieldValue(
			text = page.comment.value,
			selection = selectionRange
		),
		onValueChange = {
			selectionRange = it.selection
			listener.onCommentChanged(it.text)
		},
		placeholder = {
			if (!isFocused) {
				Text(
					modifier = Modifier.fillMaxWidth(),
					text = stringResource(R.string.account_transaction_label_comment),
					style = textStyle,
					textAlign = TextAlign.Center,
					color = colorGrey99,
				)
			}
		},
		minLines = 2,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Text,
			capitalization = KeyboardCapitalization.Sentences,
			imeAction = ImeAction.Done,
		),
		keyboardActions = KeyboardActions(
			onDone = { listener.onSaveClicked() }
		),
		isError = page.comment.error != null,
		supportingText = { TextFieldError(page.comment.error, TextAlign.Center) },
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
//			cursorColor = Color.Transparent,
//			errorCursorColor = Color.Transparent,
		),
	)
}

@Composable
private fun ColumnScope.ValueTextField(
	state: TextFieldState,
	focusedFieldCurrent: TransactionFormState.Focus,
	focusedFieldTarget: TransactionFormState.Focus,
	placeholder: String,
	onValueChange: (String) -> Unit,
	onFocusChanged: (TransactionFormState.Focus) -> Unit,
	onSaveClicked: () -> Unit,
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
				text = placeholder,
				style = textStyle,
				textAlign = TextAlign.Center,
				color = colorGrey99,
			)
		},
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
		isError = state.error != null,
		supportingText = { TextFieldError(state.error, TextAlign.Center) },
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
		keyboardActions = KeyboardActions(
			onDone = { onSaveClicked() }
		)
	)
}

//--------------------------------------------------------------------------------------------------
//  DIALOG
//--------------------------------------------------------------------------------------------------

@Composable
private fun Dialog(
	dialog: TransactionFormState.Dialog?,
	listener: TransactionFormListener,
) = when (dialog) {
	null -> Unit
	
	is TransactionFormState.Dialog.DatePicker -> SimpleDatePickerDialog(
		timestamp = dialog.timestamp,
		onDateSelected = listener::onDialogDateSelected,
		onDismiss = listener::onDialogDateDismiss
	)
	
	is TransactionFormState.Dialog.TimePicker -> SimpleTimePickerDialog(
		timestamp = dialog.timestamp,
		onTimeSelected = listener::onDialogTimeSelected,
		onDismiss = listener::onDialogTimeDismiss,
	)
	
}
