@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Kayaking
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.DraggableVerticalGrid
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.applyIf
import basilliyc.cashnote.utils.toPriceString


//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountList() {
	val viewModel = viewModel<AccountListViewModel>()
	val navController = LocalNavController.current
	Content(
		state = viewModel.state,
		draggedList = viewModel.draggedList,
		onClickAddNewAccount = {
			navController.navigate(AppNavigation.AccountForm(accountId = null))
		},
		onClickAccount = {
			navController.navigate(AppNavigation.AccountTransaction(accountId = it))
		},
		onDragStarted = viewModel::onDragStarted,
		onDragCompleted = viewModel::onDragCompleted,
		onDragReverted = viewModel::onDragReverted,
		onDragMoved = viewModel::onDragMoved,
	)
}

@Preview(showBackground = true)
@Composable
private fun AccountListPreview() = DefaultPreview {
	Content(
		state = AccountListState(
			content = AccountListState.Content.Data(
				listOf(
					FinancialAccount(
						id = 1,
						name = "Account 1",
						balance = 100.0,
						currency = AccountCurrency.UAH,
						color = null,
						position = 0,
					),
					FinancialAccount(
						id = 2,
						name = "Account 2",
						balance = 200.0,
						currency = AccountCurrency.UAH,
						color = null,
						position = 1,
					),
					FinancialAccount(
						id = 3,
						name = "Account 3",
						balance = 300.0,
						currency = AccountCurrency.UAH,
						color = null,
						position = 2,
					),
				)
			),
//			content = AccountListState.Content.Loading,
//			content = AccountListState.Content.DataEmpty,
		),
		draggedList = null,
		onClickAddNewAccount = {},
		onClickAccount = {},
		onDragStarted = {},
		onDragCompleted = { _, _ -> },
		onDragReverted = {},
		onDragMoved = { _, _ -> },
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	state: AccountListState,
	draggedList: List<FinancialAccount>?,
	onClickAddNewAccount: () -> Unit,
	onClickAccount: (id: Long) -> Unit,
	onDragStarted: () -> Unit,
	onDragCompleted: (from: Int, to: Int) -> Unit,
	onDragReverted: () -> Unit,
	onDragMoved: (from: Int, to: Int) -> Unit,
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			if (state.content is AccountListState.Content.Data) {
				ActionBar(onClickAddNewAccount)
			}
		},
		content = { innerPadding ->
			val modifier = Modifier.padding(innerPadding)
			
			when (val content = state.content) {
				is AccountListState.Content.Loading -> BoxLoading(
					modifier = modifier
				)
				
				is AccountListState.Content.Data -> ContentData(
					modifier = modifier,
					content = content,
					draggedList = draggedList,
					onClickAccount = onClickAccount,
					onDragStarted = onDragStarted,
					onDragCompleted = onDragCompleted,
					onDragReverted = onDragReverted,
					onDragMoved = onDragMoved,
				)
				
				is AccountListState.Content.DataEmpty -> ContentDataEmpty(
					modifier = modifier,
					onClickAddNewAccount = onClickAddNewAccount
				)
			}
			
		}
	)
}


//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA_EMPTY
//--------------------------------------------------------------------------------------------------

@Composable
private fun ContentDataEmpty(
	modifier: Modifier = Modifier,
	onClickAddNewAccount: () -> Unit,
) {
	Column(
		modifier = modifier
			.padding(16.dp)
			.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Image(
			modifier = Modifier.size(128.dp),
			imageVector = Icons.Filled.Kayaking,
			contentDescription = stringResource(R.string.account_list_empty_title),
			colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = stringResource(R.string.account_list_empty_title),
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.headlineMedium,
		)
		Spacer(modifier = Modifier.height(16.dp))
		OutlinedButton(
			onClick = onClickAddNewAccount,
			icon = Icons.Filled.Add,
			text = stringResource(R.string.account_list_empty_submit)
		)
	}
}

//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA
//--------------------------------------------------------------------------------------------------

@Composable
private fun ContentData(
	modifier: Modifier = Modifier,
	content: AccountListState.Content.Data,
	draggedList: List<FinancialAccount>? = null,
	onClickAccount: (id: Long) -> Unit,
	onDragStarted: () -> Unit,
	onDragCompleted: (from: Int, to: Int) -> Unit,
	onDragReverted: () -> Unit,
	onDragMoved: (from: Int, to: Int) -> Unit,
) {
	val accounts = draggedList ?: content.financialAccounts
	DraggableVerticalGrid(
		modifier = modifier.fillMaxSize(),
		columns = GridCells.Adaptive(128.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(8.dp),
		onDragStarted = onDragStarted,
		onDragCompleted = onDragCompleted,
		onDragReverted = onDragReverted,
		onDragMoved = onDragMoved,
		
	) {
		items(
			count = accounts.size,
			key = { accounts[it].id },
			itemContent = { index, isDragged ->
				ContentDataItem(
					modifier = Modifier
						.applyIf({ isDragged }) {
							this.shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
						},
					financialAccount = accounts[index],
					onClickAccount = onClickAccount,
				)
			}
		)
	}
}

@Composable
private fun ContentDataItem(
	modifier: Modifier,
	financialAccount: FinancialAccount,
	onClickAccount: (id: Long) -> Unit,
) {
	//TODO replace mock data with real
	Card(
		modifier = modifier,
		onClick = { onClickAccount(financialAccount.id) },
		colors = CardDefaults.cardColors(
			containerColor = financialAccount.color?.color ?: Color.Unspecified
		)
	) {
		Column {
			Text(text = financialAccount.name, modifier = Modifier.padding(8.dp))
			Row {
				Text(text = financialAccount.currency.symbol, modifier = Modifier.padding(horizontal = 8.dp))
				Spacer(modifier = Modifier.weight(1F))
				Text(
					text = financialAccount.balance.toPriceString(showPlus = false),
					modifier = Modifier.padding(horizontal = 8.dp)
				)
			}
			//TODO set real day difference
			Text(
				modifier = Modifier
					.padding(8.dp)
					.fillMaxWidth(),
				text = (-50.7).toPriceString(showPlus = true),
				textAlign = TextAlign.End
			)
		}
	}
}


//--------------------------------------------------------------------------------------------------
//  ACTION BAR
//--------------------------------------------------------------------------------------------------

@Composable
private fun ActionBar(
	onClickAddNewAccount: () -> Unit,
) = TopAppBar(
	title = { Text(text = stringResource(R.string.main_nav_account_list)) },
	actions = {
		IconButton(
			onClick = onClickAddNewAccount
		) {
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = stringResource(R.string.add_new_account)
			)
		}
	}
)

