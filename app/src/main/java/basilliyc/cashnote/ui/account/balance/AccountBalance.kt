package basilliyc.cashnote.ui.account.balance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextField
import basilliyc.cashnote.ui.components.TextInputState
import basilliyc.cashnote.ui.components.modifier
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.asPriceWithCoins
import basilliyc.cashnote.utils.toast

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountBalance() {
	val viewModel = viewModel<AccountBalanceViewModel>()
	val state by remember { viewModel.state }
	val navController = LocalNavController.current
	val context = LocalContext.current
	Content(
		state = state,
		onBalanceDifferenceChanged = viewModel::onBalanceDifferenceChanged,
		onBalanceNewChanged = viewModel::onBalanceNewChanged,
		onCommentChanged = viewModel::onCommentChanged,
		onSaveClicked = viewModel::onSaveClicked,
		onCancelClicked = viewModel::onCancelClicked,
	)
	
	val event = state.event
	LaunchedEffect(event) {
		when (event) {
			AccountBalanceState.Event.Cancel -> {
				navController.popBackStack()
			}
			
			AccountBalanceState.Event.Save -> {
				context.toast(R.string.account_balance_toast_save)
				navController.popBackStack()
			}
			
			AccountBalanceState.Event.SaveError -> {
				context.toast(R.string.account_balance_toast_save_error)
			}
			
			null -> Unit
		}
	}
}

@Composable
@Preview(showBackground = true)
private fun AccountBalancePreview() = DefaultPreview {
	val account = Account(
		id = 1,
		name = "Account 1",
		balance = 100.0,
		currency = AccountCurrency.UAH,
		color = null,
	)
	Content(
		state = AccountBalanceState.Page(
			content = AccountBalanceState.Content.Data(
				account = account,
				isBalanceReduce = false,
				balanceDifference = TextInputState(""),
				balanceNew = TextInputState(account.balance.asPriceWithCoins()),
				comment = TextInputState(""),
			),
		),
		onBalanceDifferenceChanged = {},
		onBalanceNewChanged = {},
		onCommentChanged = {},
		onSaveClicked = {},
		onCancelClicked = {},
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	state: AccountBalanceState.Page,
	onBalanceDifferenceChanged: (String) -> Unit,
	onBalanceNewChanged: (String) -> Unit,
	onCommentChanged: (String) -> Unit,
	onSaveClicked: () -> Unit,
	onCancelClicked: () -> Unit,
) {
	Scaffold(
		topBar = {
			ActionBar(
				state = state,
				onSaveClicked = onSaveClicked,
			)
		},
		content = {
			val content = state.content
			val modifier = Modifier
				.padding(it)
				.verticalScroll(rememberScrollState())
			
			when (content) {
				is AccountBalanceState.Content.Loading -> BoxLoading(modifier = modifier)
				is AccountBalanceState.Content.Data -> ContentData(
					modifier = modifier,
					content = content,
					onBalanceDifferenceChanged = onBalanceDifferenceChanged,
					onBalanceNewChanged = onBalanceNewChanged,
					onCommentChanged = onCommentChanged,
					onSaveClicked = onSaveClicked,
					onCancelClicked = onCancelClicked,
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
	state: AccountBalanceState.Page,
	onSaveClicked: () -> Unit,
) {
	val content = state.content
	
	SimpleActionBar(
		title = {
			if (content is AccountBalanceState.Content.Data) {
				val account = content.account
				Text(
					text = "${account.name} ${account.currency.symbol}",
					style = MaterialTheme.typography.titleLarge
				)
			}
		},
		containerColor = (content as? AccountBalanceState.Content.Data)?.account?.let {
			it.color?.color
		} ?: Color.Unspecified,
		actions = {
			IconButton(
				onClick = onSaveClicked,
				imageVector = Icons.Filled.Done,
				contentDescription = stringResource(R.string.account_balance_action_save)
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
	content: AccountBalanceState.Content.Data,
	onBalanceDifferenceChanged: (String) -> Unit,
	onBalanceNewChanged: (String) -> Unit,
	onCommentChanged: (String) -> Unit,
	onSaveClicked: () -> Unit,
	onCancelClicked: () -> Unit,
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
				.padding(
					start = 16.dp, end = 16.dp, top = 16.dp
				),
			text = stringResource(
				when (content.isBalanceReduce) {
					null -> R.string.account_balance_label_balance_not_changed
					true -> R.string.account_balance_label_balance_reduced
					false -> R.string.account_balance_label_balance_increased
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
		BalanceComment(
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
			text = stringResource(R.string.account_balance_action_save),
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
	state: TextInputState,
	onValueChanged: (String) -> Unit,
) {
	TextField(
		modifier = modifier,
		state = state,
		onValueChange = onValueChanged,
		label = { Text(text = stringResource(R.string.account_balance_label_balance_difference)) },
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
	state: TextInputState,
	onValueChanged: (String) -> Unit,
) {
	TextField(
		state = state,
		onValueChange = onValueChanged,
		label = { Text(text = stringResource(R.string.account_balance_label_balance_new)) },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Number
		),
	)
}


//--------------------------------------------------------------------------------------------------
// CONTENT.COMMENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun BalanceComment(
	state: TextInputState,
	onValueChanged: (String) -> Unit,
) {
	TextField(
		state = state,
		onValueChange = onValueChanged,
		label = { Text(text = stringResource(R.string.account_balance_label_balance_comment)) },
		singleLine = false,
		minLines = 3,
		maxLines = 8,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Text,
			capitalization = KeyboardCapitalization.Sentences,
		),
	)
}

