@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account.form

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.DialogLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountForm() {
	val viewModel = viewModel<AccountFormViewModel>()
	val state by remember { viewModel.state }
	val navController = LocalNavController.current
	Content(
		state = state,
		onCurrencyChanged = viewModel::onCurrencyChanged,
		onNameChanged = viewModel::onNameChanged,
		onBalanceChanged = viewModel::onBalanceChanged,
		onColorChanged = viewModel::onColorChanged,
		onSaveClicked = { viewModel.onSaveClicked(navController) },
	)
}

@Composable
@Preview(showBackground = true)
private fun AccountFormPreview() = DefaultPreview {
	Content(
		state = AccountFormState.Page(
			content = AccountFormState.Content.Data(
				Account(
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
	state: AccountFormState.Page,
	onCurrencyChanged: (AccountCurrency) -> Unit,
	onNameChanged: (String) -> Unit,
	onBalanceChanged: (String) -> Unit,
	onColorChanged: (AccountColor) -> Unit,
	onSaveClicked: () -> Unit,
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = { ActionBar(state = state) },
		content = { innerPadding ->
			val modifier = Modifier.padding(innerPadding)
			
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
private fun ActionBar(state: AccountFormState.Page) {
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
			value = content.name,
			error = content.nameError,
			onChanged = onNameChanged,
		)
		AccountBalance(
			value = content.balance,
			error = content.balanceError,
			onChanged = onBalanceChanged,
		)
		AccountColor(
			value = content.color,
			onChanged = onColorChanged,
		)
		Spacer(modifier = Modifier.weight(1F))
		Button(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			onClick = onSaveClicked,
			text = stringResource(R.string.account_form_action_save),
			icon = Icons.Filled.Save,
			enabled = !content.isSaving,
		)
		Spacer(modifier = Modifier.height(16.dp))
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
					.weight(1F)
					.border(
						width = 1.dp,
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = MaterialTheme.shapes.medium
					),
				onClick = { onChanged(currency) },
				colors = CardDefaults.cardColors(
					containerColor = if (currency == value) {
						MaterialTheme.colorScheme.primaryContainer
					} else {
						MaterialTheme.colorScheme.surface
					}
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
	value: String,
	error: TextFieldError?,
	onChanged: (String) -> Unit,
) {
	Spacer(modifier = Modifier.height(12.dp))
	TextField(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 4.dp),
		value = value,
		onValueChange = onChanged,
		label = { Text(text = stringResource(R.string.account_form_label_name)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			capitalization = KeyboardCapitalization.Sentences,
			keyboardType = KeyboardType.Text
		),
		isError = error != null,
		supportingText = { TextFieldError(error) }
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA.BALANCE
//--------------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.AccountBalance(
	value: String,
	error: TextFieldError?,
	onChanged: (String) -> Unit,
) {
	TextField(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 4.dp),
		value = value,
		onValueChange = onChanged,
		label = { Text(text = stringResource(R.string.account_form_label_balance)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
		isError = error != null,
		supportingText = { TextFieldError(error) }
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
		modifier = Modifier.padding(horizontal = 16.dp)
	)
	LazyVerticalGrid(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp),
		columns = GridCells.Adaptive(100.dp),
		verticalArrangement = Arrangement.spacedBy(0.dp),
		horizontalArrangement = Arrangement.spacedBy(0.dp),
	) {
		items(
			count = AccountColor.entries.size,
			key = { AccountColor.entries[it].ordinal }
		) {
			val accountColor = AccountColor.entries[it]
			Card(
				modifier = Modifier
					.animateItem()
					.fillMaxWidth()
					.padding(horizontal = 4.dp),
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
}