@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account.form

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextField
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.castOrNull
import basilliyc.cashnote.utils.toast

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountForm() {
	val viewModel = viewModel<AccountFormViewModel>()
	val state = viewModel.state
	val navController = LocalNavController.current
	val context = LocalContext.current
	Content(
		state = state,
		onCurrencyChanged = viewModel::onCurrencyChanged,
		onNameChanged = viewModel::onNameChanged,
		onBalanceChanged = viewModel::onBalanceChanged,
		onColorChanged = viewModel::onColorChanged,
		onSaveClicked = viewModel::onSaveClicked,
	)
	
	val action = state.action
	LaunchedEffect(action) {
		when (action) {
			AccountFormState.Action.Cancel -> {
				navController.popBackStack()
			}
			
			AccountFormState.Action.SaveSuccess -> {
				when (state.content.castOrNull<AccountFormState.Content.Data>()?.isNew) {
					true -> context.toast(R.string.account_form_toast_save_new)
					false -> context.toast(R.string.account_form_toast_save_update)
					null -> Unit
				}
				navController.popBackStack()
			}
			
			AccountFormState.Action.SaveError -> {
				context.toast(R.string.account_form_toast_save_error)
			}
			
			null -> Unit
		}
		viewModel.onActionConsumed()
	}
}

@Composable
@Preview(showBackground = true)
private fun AccountFormPreview() = DefaultPreview {
	Content(
		state = AccountFormState(
			content = AccountFormState.Content.Data(
				FinancialAccount(
					id = 1,
					name = "Account 1",
					balance = 100.0,
					currency = AccountCurrency.UAH,
					color = null,
				)
			),
		),
		onCurrencyChanged = {},
		onNameChanged = {},
		onBalanceChanged = {},
		onColorChanged = {},
		onSaveClicked = {},
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	state: AccountFormState,
	onCurrencyChanged: (AccountCurrency) -> Unit,
	onNameChanged: (String) -> Unit,
	onBalanceChanged: (String) -> Unit,
	onColorChanged: (AccountColor) -> Unit,
	onSaveClicked: () -> Unit,
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			ActionBar(
				state = state,
				onSaveClicked = onSaveClicked,
			)
		},
		content = { innerPadding ->
			val modifier = Modifier
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
			
			when (val content = state.content) {
				is AccountFormState.Content.Loading -> BoxLoading(
					modifier = modifier
				)
				
				is AccountFormState.Content.Data -> ContentData(
					modifier = modifier,
					content = content,
					onCurrencyChanged = onCurrencyChanged,
					onNameChanged = onNameChanged,
					onBalanceChanged = onBalanceChanged,
					onColorChanged = onColorChanged,
					onSaveClicked = onSaveClicked,
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
	state: AccountFormState,
	onSaveClicked: () -> Unit,
) {
	SimpleActionBar(
		title = {
			val content = state.content
			if (content is AccountFormState.Content.Data) {
				Text(
					text = stringResource(
						if (content.isNew) R.string.account_form_title_create
						else R.string.account_form_title_edit
					),
					style = MaterialTheme.typography.titleLarge
				)
			}
		},
		actions = {
			IconButton(
				onClick = onSaveClicked,
				imageVector = Icons.Filled.Done,
				contentDescription = stringResource(R.string.account_form_action_save)
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
	content: AccountFormState.Content.Data,
	onCurrencyChanged: (AccountCurrency) -> Unit,
	onNameChanged: (String) -> Unit,
	onBalanceChanged: (String) -> Unit,
	onColorChanged: (AccountColor) -> Unit,
	onSaveClicked: () -> Unit,
) {
	Column(
		modifier = modifier.fillMaxSize(),
	) {
		CurrencyPicker(
			value = content.currency,
			onChanged = onCurrencyChanged
		)
		AccountName(
			state = content.name,
			onChanged = onNameChanged,
		)
		AccountBalance(
			state = content.balance,
			onChanged = onBalanceChanged,
		)
		AccountColor(
			value = content.color,
			onChanged = onColorChanged,
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
			text = stringResource(R.string.account_form_action_save),
			icon = Icons.Filled.Save,
		)
	}
}

//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA.CURRENCY
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.CurrencyPicker(
	value: AccountCurrency,
	onChanged: (AccountCurrency) -> Unit,
) {
	Text(
		text = stringResource(R.string.account_form_label_currency),
		style = MaterialTheme.typography.titleMedium,
		modifier = Modifier.padding(horizontal = 16.dp)
	)
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp),
	) {
		AccountCurrency.entries.forEach { currency ->
			Card(
				modifier = Modifier
					.padding(8.dp)
					.weight(1F),
				onClick = { onChanged(currency) },
				colors = CardDefaults.cardColors(
					containerColor = if (currency == value) {
						MaterialTheme.colorScheme.primaryContainer
					} else {
						MaterialTheme.colorScheme.surface
					}
				),
				border = BorderStroke(
					width = 1.dp,
					color = MaterialTheme.colorScheme.primaryContainer,
				)
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
//  CONTENT.DATA.NAME
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.AccountName(
	state: TextFieldState,
	onChanged: (String) -> Unit,
) {
	TextField(
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
//  CONTENT.DATA.BALANCE
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.AccountBalance(
	state: TextFieldState,
	onChanged: (String) -> Unit,
) {
	TextField(
		state = state,
		onValueChange = onChanged,
		label = { Text(text = stringResource(R.string.account_form_label_balance)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA.COLOR
//--------------------------------------------------------------------------------------------------


@Composable
private fun ColumnScope.AccountColor(
	value: AccountColor?,
	onChanged: (AccountColor) -> Unit,
) {
	Text(
		text = stringResource(R.string.account_form_label_color),
		style = MaterialTheme.typography.titleMedium,
		modifier = Modifier
			.padding(horizontal = 16.dp)
			.padding(bottom = 4.dp)
	)
	
	VerticalGrid(
		modifier = Modifier.padding(horizontal = 16.dp),
		columns = VerticalGridCells.Adaptive(100.dp),
		horizontalSpace = 8.dp,
		itemsCount = AccountColor.entries.size,
	) {
		val accountColor = AccountColor.entries[it]
		Card(
			modifier = Modifier
				.fillMaxWidth(),
			colors = CardDefaults.cardColors(
				containerColor = if (accountColor == value) {
					accountColor.color
				} else Color.Unspecified,
			),
			onClick = { onChanged(accountColor) }
		) {
			Text(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp),
				text = accountColor.name,
				textAlign = TextAlign.Center,
			)
		}
	}
	
}