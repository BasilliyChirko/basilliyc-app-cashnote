package basilliyc.cashnote.ui.transaction.history

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.symbol
import basilliyc.cashnote.ui.theme.backgroundCardGradient
import basilliyc.cashnote.ui.theme.colorGrey99
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.TimestampStyle
import basilliyc.cashnote.utils.applyIf
import basilliyc.cashnote.utils.format
import basilliyc.cashnote.utils.toPriceColor
import basilliyc.cashnote.utils.toPriceString
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.util.Calendar

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun TransactionHistory() {
	val viewModel = viewModel<TransactionHistoryViewModel>()
	Result(result = viewModel.state.result, listener = viewModel)
	Page(page = viewModel.state.page, listener = viewModel)
}

@Composable
private fun Result(
	result: TransactionHistoryStateHolder.Result?,
	listener: TransactionHistoryListener,
) {
	handleResult(result, listener) {
		when (it) {
			is TransactionHistoryStateHolder.Result.EditTransaction -> {
				navigateForward(
					AppNavigation.TransactionForm(
						accountId = it.accountId,
						categoryId = it.categoryId,
						transactionId = it.transactionId
					)
				)
			}
		}
	}
}

@Composable
@Preview(showBackground = true)
private fun TransactionListPagePreview() = DefaultPreview {
	Page(
		page = TransactionHistoryStateHolder.Page(
			initialLoading = false,
			initialLoadingError = null,
		),
		listener = object : TransactionHistoryListener {
			override fun onInitialLoadingErrorSubmitted() = Unit
			override fun onTransactionsLoadingMoreErrorSubmitted() = Unit
			override fun onTransactionsLoadingMore() = Unit
			override fun onTransactionEditClicked(id: Long) = Unit
			override fun onTransactionDeleteClicked(id: Long) = Unit
			override fun onResultHandled() = Unit
		}
	)
}

@Composable
private fun Page(
	page: TransactionHistoryStateHolder.Page,
	listener: TransactionHistoryListener,
) {
	
	val listState = rememberLazyListState()
	
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				title = {
					Text(
						text =
						if (page.singleAccount != null)
							stringResource(
								R.string.account_history_title_s,
								page.singleAccount.name
							)
						else stringResource(R.string.account_history_title)
					)
				},
				actions = { },
			)
		},
		content = {
			
			when {
				page.initialLoading -> BoxLoading()
				
				page.initialLoadingError != null -> InitialLoadingError(
					onSubmitted = listener::onInitialLoadingErrorSubmitted,
				)
				
				page.transactions.isEmpty() -> TransactionsListEmpty()
				
				else -> TransactionsList(
					page = page,
					listener = listener
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
			.fillMaxSize()
			.padding(top = 64.dp),
		verticalArrangement = Arrangement.Top,
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
	page: TransactionHistoryStateHolder.Page,
	listener: TransactionHistoryListener,
) {
	val transactions = page.transactions
	
	val isLoading = page.transactionsLoadingMore
	val buffer = 2
	
	val listState = rememberLazyListState()
	
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
				listener.onTransactionsLoadingMore()
			}
	}
	
	val calendar = Calendar.getInstance()
	var year = 0
	var month = 0
	var day = 0
	
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		state = listState,
		contentPadding = PaddingValues(
			top = 16.dp,
			start = 8.dp,
			end = 8.dp,
			bottom = 16.dp,
		),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		//Usual items
		
		for (index in transactions.indices) {
			calendar.timeInMillis = transactions[index].date
			if (calendar[Calendar.YEAR] != year ||
				calendar[Calendar.MONTH] != month ||
				calendar[Calendar.DAY_OF_MONTH] != day
			) {
				
				year = calendar[Calendar.YEAR]
				month = calendar[Calendar.MONTH]
				day = calendar[Calendar.DAY_OF_MONTH]
				
				TransactionListItemDate(page, listener, calendar.timeInMillis)
			}
			
			TransactionListItem(page, listener, index)
		}
		
		
		/*		items(
					count = transactions.size,
					key = { transactions[it].id },
					contentType = { transactions[it].javaClass },
					itemContent = { index ->
						val transaction = transactions[index]
						val category = page.categories[transaction.categoryId]
						val account = if (page.singleAccount != null) page.singleAccount
						else page.accounts[transaction.accountId]
						PopupMenu(
							modifier = Modifier.animateItem(),
							anchor = {
								TransactionListItem(
									account = account,
									category = category,
									transaction = transaction,
									showAccount = page.singleAccount == null,
									onClick = { expand() },
								)
							},
							items = {
								PopupMenuItem(
									text = stringResource(R.string.account_history_action_edit_transaction),
									onClick = {
										collapse()
										listener.onTransactionEditClicked(transaction.id)
									},
									leadingIcon = Icons.Filled.Edit,
								)
								PopupMenuItem(
									text = stringResource(R.string.account_history_action_delete_transaction),
									onClick = {
										collapse()
										listener.onTransactionDeleteClicked(transaction.id)
									},
									leadingIcon = Icons.Filled.DeleteForever,
								)
							}
						)
					}
				)*/
		
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
		if (page.transactionsLoadingMoreError != null) {
			item(
				key = page.transactionsLoadingMoreError,
				contentType = page.transactionsLoadingMoreError.javaClass,
				content = {
					Column(
						modifier = Modifier
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
							onClick = listener::onTransactionsLoadingMoreErrorSubmitted,
						)
					}
				}
			)
		}
	}
}

@Suppress("FunctionName")
private fun LazyListScope.TransactionListItemDate(
	page: TransactionHistoryStateHolder.Page,
	listener: TransactionHistoryListener,
	date: Long,
) {
	item(
		key = date,
		contentType = Long::class,
		content = {
			Row(
				modifier = Modifier
					.animateItem()
					.fillMaxWidth()
					.padding(4.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Center,
			) {
				Text(
					text = date.format(TimestampStyle.YearMonthDay),
					color = colorGrey99,
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	)
}

@Suppress("FunctionName")
private fun LazyListScope.TransactionListItem(
	page: TransactionHistoryStateHolder.Page,
	listener: TransactionHistoryListener,
	index: Int,
) {
	val transaction = page.transactions[index]
	item(
		key = transaction.id,
		contentType = transaction.javaClass,
		content = {
			val category = page.categories[transaction.categoryId]
			val account = if (page.singleAccount != null) page.singleAccount
			else page.accounts[transaction.accountId]
			PopupMenu(
				modifier = Modifier.animateItem(),
				anchor = {
					TransactionListItem(
						account = account,
						category = category,
						transaction = transaction,
						showAccount = page.singleAccount == null,
						onClick = { expand() },
					)
				},
				items = {
					PopupMenuItem(
						text = stringResource(R.string.account_history_action_edit_transaction),
						onClick = {
							collapse()
							listener.onTransactionEditClicked(transaction.id)
						},
						leadingIcon = Icons.Filled.Edit,
					)
					PopupMenuItem(
						text = stringResource(R.string.account_history_action_delete_transaction),
						onClick = {
							collapse()
							listener.onTransactionDeleteClicked(transaction.id)
						},
						leadingIcon = Icons.Filled.DeleteForever,
					)
				}
			)
		}
	)
}

@Composable
private fun LazyItemScope.TransactionListItem(
	modifier: Modifier = Modifier,
	showAccount: Boolean,
	account: FinancialAccount?,
	category: FinancialCategory?,
	transaction: FinancialTransaction,
	onClick: (id: Long) -> Unit,
) {
	OutlinedCard(
		modifier = modifier.fillMaxWidth(),
		onClick = { onClick(transaction.id) },
	) {
		Column(
			modifier = Modifier
				.applyIf({ showAccount && account != null && account.color != null }) {
					backgroundCardGradient(account!!.color!!)
				}
		) {
			Row(
				modifier = Modifier
					.padding(16.dp)
			) {
				Column {
					if (category != null) {
						Row {
							if (category.icon != null) {
								Icon(
									imageVector = category.icon.imageVector,
									contentDescription = null,
									modifier = Modifier.padding(end = 8.dp)
								)
							}
							Text(text = category.name)
						}
					}
					if (showAccount && account != null) {
						Text(text = "${account.currency.symbol} ${account.name}")
					}
				}
				Spacer(modifier = Modifier.weight(1F))
				Column(
					horizontalAlignment = Alignment.End
				) {
					Text(
						text = buildString {
							append(transaction.value.toPriceString(showPlus = true))
							if (account != null) {
								append(" ${account.currency.symbol}")
							}
						},
						color = transaction.value.toPriceColor(),
						textAlign = TextAlign.End,
					)
					
					transaction.comment?.let { comment ->
						Text(
							text = comment,
							textAlign = TextAlign.End,
							color = colorGrey99,
							style = MaterialTheme.typography.bodyMedium,
						)
					}
				}
			}
			
		}
	}
}