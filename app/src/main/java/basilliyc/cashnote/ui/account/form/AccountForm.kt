@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.components.CardSelectable
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.OutlinedTextField
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.components.menu.MenuRowPopupColor
import basilliyc.cashnote.ui.components.menu.MenuRowSwitch
import basilliyc.cashnote.ui.symbol
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.rememberSingleRunner
import basilliyc.cashnote.utils.showToast

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountForm() {
	val viewModel = viewModel<AccountFormViewModel>()
	Page(state = viewModel.state, listener = viewModel)
	Action(action = viewModel.state.action, listener = viewModel)
}

//--------------------------------------------------------------------------------------------------
//  ACTION
//--------------------------------------------------------------------------------------------------

@Composable
private fun Action(
	action: AccountFormState.Action?,
	listener: AccountFormListener,
) {
	
	val context = LocalContext.current
	val navController = LocalNavController.current
	val singleRunner = rememberSingleRunner()
	
	val action = action
	LaunchedEffect(action) {
		when (action) {
			AccountFormState.Action.Cancel -> {
				singleRunner.schedule {
					navController.popBackStack()
				}
			}
			
			is AccountFormState.Action.SaveSuccess -> {
				singleRunner.schedule {
					if (action.isNeedRebuildApp) {
						//Do nothing
						
//						val packageManager: PackageManager = context.packageManager
//						val intent: Intent =
//							packageManager.getLaunchIntentForPackage(context.packageName)!!
//						intent.flags += Intent.FLAG_ACTIVITY_CLEAR_TOP
//						context.startActivity(intent)
						
					} else {
						when (action.isNew) {
							true -> context.showToast(R.string.account_form_toast_save_new)
							false -> context.showToast(R.string.account_form_toast_save_update)
						}
						navController.popBackStack()
					}
				}
			}
			
			AccountFormState.Action.SaveError -> {
				singleRunner.schedule {
					context.showToast(R.string.account_form_toast_save_error)
				}
			}
			
			null -> Unit
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
		state = AccountFormState(
			page = AccountFormState.Page.Data(
				account = PreviewValues.accountTestUSD,
				isShowOnNavigation = true
			),
		),
		listener = object : AccountFormListener {},
	)
}


@Composable
private fun Page(
	state: AccountFormState,
	listener: AccountFormListener,
) {
	when (val page = state.page) {
		is AccountFormState.Page.Data -> PageData(page = page, listener = listener)
		AccountFormState.Page.Loading -> PageLoading()
	}
}


@Composable
private fun PageData(
	page: AccountFormState.Page.Data,
	listener: AccountFormListener,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				title = stringResource(
					if (page.isNew) R.string.account_form_title_create
					else R.string.account_form_title_edit
				),
				actions = {
					IconButton(
						onClick = listener::onSaveClicked,
						imageVector = Icons.Filled.Done,
						contentDescription = stringResource(R.string.account_form_action_save)
					)
				}
			)
		},
		content = {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState()),
			) {
				CurrencyPicker(
					value = page.currency,
					onChanged = listener::onCurrencyChanged
				)
				AccountName(
					state = page.name,
					onChanged = listener::onNameChanged,
				)
				AccountBalance(
					state = page.balance,
					onChanged = listener::onBalanceChanged,
				)
				MenuRowPopupColor(
					title = stringResource(R.string.account_form_label_color),
					color = page.color,
					onColorSelected = listener::onColorChanged,
				)
				MenuRowSwitch(
					title = stringResource(R.string.account_form_show_on_navigation),
					checked = page.isShowOnNavigation,
					onCheckedChange = listener::onShowOnNavigationChanged,
				)
			}
		},
	)
}

//---------------------------x-----------------------------------------------------------------------
//  ACCOUNT CURRENCY
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.CurrencyPicker(
	value: FinancialCurrency,
	onChanged: (FinancialCurrency) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp),
	) {
		FinancialCurrency.entries.forEach { currency ->
			CardSelectable(
				modifier = Modifier
					.padding(8.dp)
					.weight(1F),
				onClick = { onChanged(currency) },
				isSelected = currency == value,
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(8.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					Text(text = currency.symbol, style = MaterialTheme.typography.displaySmall)
					Text(text = currency.name)
				}
			}
		}
	}
}

//--------------------------------------------------------------------------------------------------
//  ACCOUNT NAME
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.AccountName(
	state: TextFieldState,
	onChanged: (String) -> Unit,
) {
	OutlinedTextField(
		state = state,
		onValueChange = onChanged,
		label = { Text(text = stringResource(R.string.account_form_label_name)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			capitalization = KeyboardCapitalization.Sentences,
			keyboardType = KeyboardType.Text
		),
	)
}

//--------------------------------------------------------------------------------------------------
//  ACCOUNT BALANCE
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.AccountBalance(
	state: TextFieldState,
	onChanged: (String) -> Unit,
) {
	OutlinedTextField(
		state = state,
		onValueChange = onChanged,
		label = { Text(text = stringResource(R.string.account_form_label_balance)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
	)
}
