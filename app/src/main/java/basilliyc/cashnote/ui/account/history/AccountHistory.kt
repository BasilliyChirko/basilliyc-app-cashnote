package basilliyc.cashnote.ui.account.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.TimestampStyle
import basilliyc.cashnote.utils.format
import basilliyc.cashnote.utils.toPriceWithCoins
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountHistory() {
	
	val viewModel = viewModel<AccountHistoryViewModel>()
	
	Content(
		state = viewModel.state,
		onInitialLoadingErrorSubmitted = viewModel::onInitialLoadingErrorSubmitted,
		onLoadingMoreErrorSubmitted = viewModel::onTransactionsLoadingMoreErrorSubmitted,
		onLoadingMore = viewModel::onTransactionsLoadingMore,
		onTransactionClicked = viewModel::onTransactionClicked,
	)
}

@Composable
@Preview(showBackground = true)
private fun AccountHistoryPreview() = DefaultPreview {
	Content(
		state = AccountHistoryState(
			initialLoading = false,
			initialLoadingError = null,
		),
		onInitialLoadingErrorSubmitted = {},
		onLoadingMoreErrorSubmitted = {},
		onLoadingMore = {},
		onTransactionClicked = {},
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	state: AccountHistoryState,
	onInitialLoadingErrorSubmitted: () -> Unit,
	onLoadingMoreErrorSubmitted: () -> Unit,
	onLoadingMore: () -> Unit,
	onTransactionClicked: (id: Long) -> Unit,
) {
	
	val listState = rememberLazyListState()
	
	Scaffold(
		topBar = {
			SimpleActionBar(
				title = {
					Text(
						text =
						if (state.account != null)
							stringResource(R.string.account_history_title_s, state.account.name)
						else stringResource(R.string.account_history_title)
					)
				},
				actions = { },
			)
		},
		content = {
			val modifier = Modifier.padding(it)
			
			when {
				state.initialLoading -> BoxLoading(modifier)
				
				state.initialLoadingError != null -> InitialLoadingError(
					modifier = modifier,
					onSubmitted = onInitialLoadingErrorSubmitted,
				)
				
				state.transactions.isEmpty() -> TransactionsListEmpty(modifier)
				
				else -> TransactionsList(
					modifier = modifier,
					state = state,
					listState = listState,
					onLoadingMoreErrorSubmitted = onLoadingMoreErrorSubmitted,
					onLoadingMore = onLoadingMore,
					onTransactionClicked = onTransactionClicked,
				)
				
			}
			
		}
	)
}

//--------------------------------------------------------------------------------------------------
//  INITIAL LOADING ERROR
//--------------------------------------------------------------------------------------------------

@Composable
private fun InitialLoadingError(
	modifier: Modifier = Modifier,
	onSubmitted: () -> Unit,
) {
	Column(
		modifier = modifier
			.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(text = stringResource(R.string.account_history_loading_error))
		Spacer(modifier = Modifier.height(8.dp))
		Button(
			text = stringResource(R.string.account_history_loading_error_retry),
			onClick = onSubmitted,
		)
	}
}

//--------------------------------------------------------------------------------------------------
//  EMPTY TRANSACTION LIST
//--------------------------------------------------------------------------------------------------

@Composable
private fun TransactionsListEmpty(
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(text = stringResource(R.string.account_history_empty_label))
	}
}

//--------------------------------------------------------------------------------------------------
//  TRANSACTIONS LIST
//--------------------------------------------------------------------------------------------------

@Composable
private fun TransactionsList(
	modifier: Modifier = Modifier,
	state: AccountHistoryState,
	listState: LazyListState,
	onLoadingMoreErrorSubmitted: () -> Unit,
	onLoadingMore: () -> Unit,
	onTransactionClicked: (id: Long) -> Unit,
) {
	val transactions = state.transactions
	
	
	val isLoading = state.transactionsLoadingMore
	val buffer = 2
	
	// Derived state to determine when to load more items
	val shouldLoadMore = remember {
		derivedStateOf {
			// Get the total number of items in the list
			val totalItemsCount = listState.layoutInfo.totalItemsCount
			// Get the index of the last visible item
			val lastVisibleItemIndex =
				listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
			// Check if we have scrolled near the end of the list and more items should be loaded
			lastVisibleItemIndex >= (totalItemsCount - buffer) && !isLoading
		}
	}
	
	// Launch a coroutine to load more items when shouldLoadMore becomes true
	LaunchedEffect(listState) {
		snapshotFlow { shouldLoadMore.value }
			.distinctUntilChanged()
			.filter { it }  // Ensure that we load more items only when needed
			.collect {
				onLoadingMore()
			}
	}
	
	LazyColumn(
		modifier = modifier.fillMaxSize(),
		state = listState,
		contentPadding = PaddingValues(
			start = 16.dp,
			end = 16.dp,
			bottom = 16.dp,
		),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		//Usual items
		items(
			count = transactions.size,
			key = { transactions[it].id },
			contentType = { transactions[it].javaClass },
			itemContent = { index ->
				TransactionListItem(
					modifier = Modifier.animateItem(),
					transaction = transactions[index],
					onClick = onTransactionClicked,
				)
			}
		)
		
		//Loading item
		if (isLoading) {
			item(
				key = isLoading,
				contentType = isLoading.javaClass,
				content = {
					Box(
						modifier = Modifier
							.animateItem()
							.fillMaxWidth()
							.padding(16.dp),
						contentAlignment = Alignment.Center
					) {
						CircularProgressIndicator()
					}
				}
			)
		}
		
		//Loading error item
		if (state.transactionsLoadingMoreError != null) {
			item(
				key = state.transactionsLoadingMoreError,
				contentType = state.transactionsLoadingMoreError.javaClass,
				content = {
					Column(
						modifier = modifier
							.animateItem()
							.fillMaxWidth()
							.padding(16.dp),
						verticalArrangement = Arrangement.Center,
						horizontalAlignment = Alignment.CenterHorizontally,
					) {
						Text(text = stringResource(R.string.account_history_loading_error))
						Spacer(modifier = Modifier.height(8.dp))
						Button(
							text = stringResource(R.string.account_history_loading_error_retry),
							onClick = onLoadingMoreErrorSubmitted,
						)
					}
				}
			)
		}
	}
}

@Composable
private fun LazyItemScope.TransactionListItem(
	modifier: Modifier = Modifier,
	transaction: FinancialTransaction,
	onClick: (id: Long) -> Unit,
) {
	Card(
		modifier = modifier
			.fillMaxWidth(),
		onClick = { onClick(transaction.id) },
	) {
		Row(
			modifier = Modifier
				.padding(16.dp)
		) {
			Text(text = transaction.value.toPriceWithCoins())
			Spacer(modifier = Modifier.weight(1F))
			Text(text = transaction.date.format(TimestampStyle.YearMonthDayHourMinute))
		}
	}
}