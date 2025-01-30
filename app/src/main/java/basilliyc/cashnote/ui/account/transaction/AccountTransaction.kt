@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.data.FinancialTransactionCategoryIcon
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextField
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.components.modifier
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.TimestampStyle
import basilliyc.cashnote.utils.toPriceWithCoins
import basilliyc.cashnote.utils.format
import basilliyc.cashnote.utils.toast
import java.util.Calendar

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountTransaction() {
	val viewModel = viewModel<AccountTransactionViewModel>()
	val state = viewModel.state
	val navController = LocalNavController.current
	val context = LocalContext.current
	Content(
		state = state,
		onBalanceDifferenceChanged = viewModel::onBalanceDifferenceChanged,
		onBalanceNewChanged = viewModel::onBalanceNewChanged,
		onCommentChanged = viewModel::onCommentChanged,
		onSaveClicked = viewModel::onSaveClicked,
		onCancelClicked = viewModel::onCancelClicked,
		onCategoryChanged = viewModel::onCategoryChanged,
		onAccountEditClicked = viewModel::onAccountEditClicked,
		onAccountDeleteClicked = viewModel::onAccountDeleteClicked,
		onAccountHistoryClicked = viewModel::onAccountHistoryClicked,
		onDateClicked = viewModel::onDateClicked,
		onTimeClicked = viewModel::onTimeClicked,
	)
	
	val action = state.action
	LaunchedEffect(action) {
		when (action) {
			null -> Unit
			
			AccountTransactionState.Action.Cancel -> {
				navController.popBackStack()
			}
			
			AccountTransactionState.Action.SaveSuccess -> {
				navController.popBackStack()
			}
			
			AccountTransactionState.Action.SaveError -> {
				context.toast(R.string.account_transaction_toast_save_error)
			}
			
			AccountTransactionState.Action.AccountDeletionSuccess -> {
				navController.popBackStack()
				context.toast(R.string.account_transaction_toast_account_deletion_success)
			}
			
			AccountTransactionState.Action.AccountDeletionError -> {
				context.toast(R.string.account_transaction_toast_account_deletion_error)
			}
			
			is AccountTransactionState.Action.AccountEdit -> {
				navController.navigate(AppNavigation.AccountForm(action.accountId))
			}
			
			is AccountTransactionState.Action.AccountHistory -> {
				navController.navigate(AppNavigation.AccountHistory(action.accountId))
			}
		}
		viewModel.onActionConsumed()
	}
	
	when (val dialogState = state.dialog) {
		null -> Unit
		AccountTransactionState.Dialog.AccountDeleteConfirmation -> DialogDeleteConfirmation(
			onConfirm = viewModel::onAccountDeleteDialogConfirmed,
			onCancel = viewModel::onAccountDeleteDialogCanceled,
		)
		
		is AccountTransactionState.Dialog.DatePicker -> TransactionDatePickerDialog(
			timestamp = dialogState.timestamp,
			onDateSelected = viewModel::onDialogDateSelected,
			onDismiss = viewModel::onDialogDateDismiss,
		)
		
		is AccountTransactionState.Dialog.TimePicker -> TransactionTimePickerDialog(
			timestamp = dialogState.timestamp,
			onTimeSelected = viewModel::onDialogTimeSelected,
			onDismiss = viewModel::onDialogTimeDismiss,
		)
	}
}

@Composable
@Preview(showBackground = true)
private fun AccountTransactionPreview() = DefaultPreview {
	val financialAccount = FinancialAccount(
		id = 1,
		name = "Account 1",
		balance = 100.0,
		currency = AccountCurrency.UAH,
		color = null,
		position = 0,
	)
	val availableCategories = listOf(
		FinancialTransactionCategory(
			id = 1,
			name = "Home",
			icon = FinancialTransactionCategoryIcon.Home,
		),
		FinancialTransactionCategory(
			id = 2,
			name = "Person",
			icon = FinancialTransactionCategoryIcon.Person,
		),
		FinancialTransactionCategory(
			id = 3,
			name = "Other",
			icon = null,
		),
	)
	Content(
		state = AccountTransactionState(
			content = AccountTransactionState.Content.Data(
				financialAccount = financialAccount,
				isBalanceReduce = false,
				balanceDifference = TextFieldState(""),
				balanceNew = TextFieldState(financialAccount.balance.toPriceWithCoins()),
				comment = TextFieldState(""),
				availableCategories = availableCategories,
				selectedCategoryId = availableCategories.getOrNull(0)?.id,
				timestamp = System.currentTimeMillis(),
			),
		),
		onBalanceDifferenceChanged = {},
		onBalanceNewChanged = {},
		onCommentChanged = {},
		onSaveClicked = {},
		onCancelClicked = {},
		onCategoryChanged = {},
		onAccountEditClicked = {},
		onAccountDeleteClicked = {},
		onAccountHistoryClicked = {},
		onDateClicked = {},
		onTimeClicked = {},
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	state: AccountTransactionState,
	onBalanceDifferenceChanged: (String) -> Unit,
	onBalanceNewChanged: (String) -> Unit,
	onCommentChanged: (String) -> Unit,
	onCategoryChanged: (Long?) -> Unit,
	onSaveClicked: () -> Unit,
	onCancelClicked: () -> Unit,
	onAccountEditClicked: () -> Unit,
	onAccountDeleteClicked: () -> Unit,
	onAccountHistoryClicked: () -> Unit,
	onDateClicked: () -> Unit,
	onTimeClicked: () -> Unit,
) {
	Scaffold(
		topBar = {
			ActionBar(
				state = state,
				onSaveClicked = onSaveClicked,
				onAccountEditClicked = onAccountEditClicked,
				onAccountDeleteClicked = onAccountDeleteClicked,
				onAccountHistoryClicked = onAccountHistoryClicked,
			)
		},
		content = {
			val content = state.content
			val modifier = Modifier
				.padding(it)
				.verticalScroll(rememberScrollState())
			
			when (content) {
				is AccountTransactionState.Content.Loading -> BoxLoading(modifier = modifier)
				is AccountTransactionState.Content.Data -> ContentData(
					modifier = modifier,
					content = content,
					onBalanceDifferenceChanged = onBalanceDifferenceChanged,
					onBalanceNewChanged = onBalanceNewChanged,
					onCommentChanged = onCommentChanged,
					onSaveClicked = onSaveClicked,
					onCategoryChanged = onCategoryChanged,
					onDateClicked = onDateClicked,
					onTimeClicked = onTimeClicked,
				)
			}
		}
	)
}

//--------------------------------------------------------------------------------------------------
//  ACTION BAR
//--------------------------------------------------------------------------------------------------

@Composable
private fun ActionBar(
	state: AccountTransactionState,
	onSaveClicked: () -> Unit,
	onAccountEditClicked: () -> Unit,
	onAccountDeleteClicked: () -> Unit,
	onAccountHistoryClicked: () -> Unit,
) {
	val content = state.content
	
	SimpleActionBar(
		title = {
			if (content is AccountTransactionState.Content.Data) {
				val account = content.financialAccount
				Text(
					text = "${account.name} ${account.currency.symbol}",
					style = MaterialTheme.typography.titleLarge
				)
			}
		},
		containerColor = (content as? AccountTransactionState.Content.Data)?.financialAccount?.let {
			it.color?.color
		} ?: Color.Unspecified,
		actions = {
			
			var isOptionsExpanded = remember { mutableStateOf(false) }
			PopupMenu(
				expanded = isOptionsExpanded,
				anchor = {
					IconButton(
						onClick = { isOptionsExpanded.value = !isOptionsExpanded.value },
						imageVector = Icons.Filled.MoreVert,
						contentDescription = stringResource(R.string.account_transaction_action_options)
					)
				},
				items = {
					PopupMenuItem(
						text = stringResource(R.string.account_transaction_action_edit_accout),
						onClick = {
							isOptionsExpanded.value = false
							onAccountEditClicked()
						},
						leadingIcon = Icons.Filled.Edit,
					)
					PopupMenuItem(
						text = stringResource(R.string.account_transaction_action_history),
						onClick = {
							isOptionsExpanded.value = false
							onAccountHistoryClicked()
						},
						leadingIcon = Icons.Filled.History,
					)
					PopupMenuItem(
						text = stringResource(R.string.account_transaction_action_delete_accout),
						onClick = {
							isOptionsExpanded.value = false
							onAccountDeleteClicked()
						},
						leadingIcon = Icons.Filled.DeleteForever,
					)
				}
			)
			
			
			
			IconButton(
				onClick = onSaveClicked,
				imageVector = Icons.Filled.Done,
				contentDescription = stringResource(R.string.account_transaction_action_save)
			)
		}
	)
}


//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA
//--------------------------------------------------------------------------------------------------

@Composable
private fun ContentData(
	modifier: Modifier = Modifier,
	content: AccountTransactionState.Content.Data,
	onBalanceDifferenceChanged: (String) -> Unit,
	onBalanceNewChanged: (String) -> Unit,
	onCategoryChanged: (Long?) -> Unit,
	onCommentChanged: (String) -> Unit,
	onSaveClicked: () -> Unit,
	onDateClicked: () -> Unit,
	onTimeClicked: () -> Unit,
) {
	val focusRequester = remember { FocusRequester() }
	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
	}
	
	Column(
		modifier = modifier.fillMaxSize()
	) {
		
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(top = 8.dp),
			text = stringResource(
				when (content.isBalanceReduce) {
					null -> R.string.account_transaction_label_balance_not_changed
					true -> R.string.account_transaction_label_balance_reduced
					false -> R.string.account_transaction_label_balance_increased
				}
			),
			style = MaterialTheme.typography.titleMedium,
			textAlign = TextAlign.End,
		)
		
		BalanceDifference(
			modifier = TextFieldDefaults.modifier
				.focusRequester(focusRequester),
			state = content.balanceDifference,
			onValueChanged = onBalanceDifferenceChanged,
		)
		BalanceNew(
			state = content.balanceNew,
			onValueChanged = onBalanceNewChanged,
		)
		TransactionCategory(
			availableCategories = content.availableCategories,
			selectedCategoryId = content.selectedCategoryId,
			onCategoryChanged = onCategoryChanged,
		)
		TransactionTimestamp(
			timestamp = content.timestamp,
			onDateClicked = onDateClicked,
			onTimeClicked = onTimeClicked,
		)
		TransactionComment(
			state = content.comment,
			onValueChanged = onCommentChanged,
		)
		Button(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					top = 32.dp,
					start = 16.dp,
					end = 16.dp,
					bottom = 16.dp
				),
			onClick = onSaveClicked,
			text = stringResource(R.string.account_transaction_action_save),
			icon = Icons.Filled.Save,
		)
	}
}

//--------------------------------------------------------------------------------------------------
// CONTENT.BALANCE_DIFFERENCE
//--------------------------------------------------------------------------------------------------

@Composable
private fun BalanceDifference(
	modifier: Modifier = Modifier,
	state: TextFieldState,
	onValueChanged: (String) -> Unit,
) {
	TextField(
		modifier = modifier,
		state = state,
		onValueChange = onValueChanged,
		label = { Text(text = stringResource(R.string.account_transaction_label_balance_difference)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
	)
}

//--------------------------------------------------------------------------------------------------
// CONTENT.BALANCE_NEW
//--------------------------------------------------------------------------------------------------

@Composable
private fun BalanceNew(
	state: TextFieldState,
	onValueChanged: (String) -> Unit,
) {
	TextField(
		state = state,
		onValueChange = onValueChanged,
		label = { Text(text = stringResource(R.string.account_transaction_label_balance_new)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
	)
}

//--------------------------------------------------------------------------------------------------
// CONTENT.CATEGORY
//--------------------------------------------------------------------------------------------------

@Composable
private fun TransactionCategory(
	availableCategories: List<FinancialTransactionCategory>,
	selectedCategoryId: Long?,
	onCategoryChanged: (Long?) -> Unit,
) {
	val navController = LocalNavController.current
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = stringResource(R.string.account_transaction_category_label)
		)
		Spacer(modifier = Modifier.weight(1f))
		IconButton(
			onClick = {
				navController.navigate(AppNavigation.TransactionCategoryList)
			},
			imageVector = Icons.Filled.Settings,
			contentDescription = stringResource(R.string.account_transaction_category_settings),
		)
	}
	
	FlowRow(
		modifier = Modifier
			.padding(horizontal = 16.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		availableCategories.forEach { category ->
			Card(
				modifier = Modifier,
				onClick = {
					onCategoryChanged(category.id.takeIf { it != selectedCategoryId })
				},
				colors = CardDefaults.cardColors(
					containerColor = if (category.id == selectedCategoryId) {
						MaterialTheme.colorScheme.primaryContainer
					} else {
						MaterialTheme.colorScheme.surface
					}
				),
				border = BorderStroke(
					width = 1.dp,
					color = MaterialTheme.colorScheme.primaryContainer,
				),
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					category.icon?.imageVector?.let { icon ->
						Icon(
							imageVector = icon,
							contentDescription = category.name,
							modifier = Modifier.padding(start = 8.dp)
						)
					}
					Text(
						modifier = Modifier.padding(8.dp),
						text = category.name,
						maxLines = 1,
					)
				}
			}
		}
	}
	
	if (availableCategories.isNotEmpty()) {
		Spacer(modifier = Modifier.height(8.dp))
	}
}

//--------------------------------------------------------------------------------------------------
// CONTENT.TIMESTAMP
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.TransactionTimestamp(
	timestamp: Long,
	onDateClicked: () -> Unit,
	onTimeClicked: () -> Unit,
) {
	Column(
		modifier = Modifier
			.padding(horizontal = 16.dp)
	) {
		Text(text = stringResource(R.string.account_transaction_label_timestamp))
		Row {
			OutlinedButton(
				modifier = Modifier.weight(1F),
				onClick = onDateClicked,
				text = timestamp.format(TimestampStyle.YearMonthDay),
			)
			Spacer(modifier = Modifier.width(16.dp))
			OutlinedButton(
				modifier = Modifier.weight(1F),
				onClick = onTimeClicked,
				text = timestamp.format(TimestampStyle.HourMinute),
			)
		}
	}
}

@Composable
private fun TransactionDatePickerDialog(
	timestamp: Long,
	onDateSelected: (Long) -> Unit,
	onDismiss: () -> Unit,
) {
	val datePickerState = rememberDatePickerState(
		initialSelectedDateMillis = timestamp
	)
	
	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = {
				onDateSelected(datePickerState.selectedDateMillis!!)
				onDismiss()
			}) {
				Text(text = stringResource(R.string.account_transaction_date_picker_confirm))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.account_transaction_date_picker_cancel))
			}
		}
	) {
		DatePicker(state = datePickerState)
	}
}

@Composable
private fun TransactionTimePickerDialog(
	timestamp: Long,
	onTimeSelected: (hour: Int, minute: Int) -> Unit,
	onDismiss: () -> Unit,
) {
	val initials = remember {
		Calendar.getInstance().apply {
			timeInMillis = timestamp
		}
	}
	val timePickerState = rememberTimePickerState(
		initialHour = initials.get(Calendar.HOUR_OF_DAY),
		initialMinute = initials.get(Calendar.MINUTE),
		is24Hour = true,
	)
	
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = {
				onTimeSelected(timePickerState.hour, timePickerState.minute)
				onDismiss()
			}) {
				Text(text = stringResource(R.string.account_transaction_date_picker_confirm))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.account_transaction_date_picker_cancel))
			}
		},
		text = {
			TimePicker(state = timePickerState)
		}
	)
	
}

//--------------------------------------------------------------------------------------------------
// CONTENT.COMMENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun TransactionComment(
	state: TextFieldState,
	onValueChanged: (String) -> Unit,
) {
	TextField(
		state = state,
		onValueChange = onValueChanged,
		label = { Text(text = stringResource(R.string.account_transaction_label_comment)) },
		singleLine = false,
		minLines = 3,
		maxLines = 8,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Text,
			capitalization = KeyboardCapitalization.Sentences,
		),
	)
}

//--------------------------------------------------------------------------------------------------
// DIALOG.DELETE_CONFIRMATION
//--------------------------------------------------------------------------------------------------

@Composable
private fun DialogDeleteConfirmation(
	onConfirm: () -> Unit,
	onCancel: () -> Unit,
) {
	AlertDialog(
		title = {
			Text(text = stringResource(R.string.account_transaction_account_delete_confirmation_title))
		},
		text = {
			Text(text = stringResource(R.string.account_transaction_account_delete_confirmation_text))
		},
		onDismissRequest = onCancel,
		confirmButton = {
			TextButton(
				onClick = onConfirm,
				content = {
					Text(text = stringResource(R.string.account_transaction_account_delete_confirmation_submit))
				}
			)
		},
		dismissButton = {
			TextButton(
				onClick = onCancel,
				content = {
					Text(text = stringResource(R.string.account_transaction_account_delete_confirmation_cancel))
				}
			)
		}
	)
}