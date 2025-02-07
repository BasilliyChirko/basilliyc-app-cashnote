package basilliyc.cashnote.ui.account.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingSource
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.activity.AppNavigation
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.launch

class AccountHistoryViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	companion object {
		const val PAGE_SIZE_INITIAL = 100
		const val PAGE_SIZE_NEXT = 50
	}
	
	var state by mutableStateOf(AccountHistoryState())
		private set
	
	private val route: AppNavigation.AccountHistory = savedStateHandle.toRoute()
	private var pagingSource = financialManager.getTransactionListPagingSource(route.accountId)
	private var nextPageKey: Int? = null
	
	//Initial loading account and data
	init {
		state = state.copy(initialLoading = true)
		viewModelScope.launch {
			val account = (financialManager.getAccountById(route.accountId)
				?: throw IllegalStateException("Account with id ${route.accountId} is not present in database"))
			state = state.copy(account = account)
			refreshTransactions(requestedSize = PAGE_SIZE_INITIAL, isInitialLoading = true)
		}
	}
	
	private fun refreshTransactions(requestedSize: Int, isInitialLoading: Boolean) {
		state = state.copy(
			initialLoading = isInitialLoading,
			transactions = if (isInitialLoading) emptyList() else state.transactions,
			transactionsLoadingMoreError = null,
		)
		schedule {
			val refreshLoadResult = pagingSource.load(
				params = PagingSource.LoadParams.Refresh<Int>(
					key = null,
					loadSize = requestedSize,
					placeholdersEnabled = false,
				)
			)
			
			if (refreshLoadResult is PagingSource.LoadResult.Error) {
				state = state.copy(
					initialLoadingError = refreshLoadResult.throwable
				)
			} else {
				applyLoadingResult(refreshLoadResult, replaceTransactions = true)
			}
			
			state = state.copy(
				initialLoading = false
			)
		}
	}
	
	private val onInvalidatedCallback: () -> Unit = {
		pagingSource.unregisterInvalidatedCallback(onInvalidatedCallback)
		pagingSource = financialManager.getTransactionListPagingSource(route.accountId)
		pagingSource.registerInvalidatedCallback(onInvalidatedCallback)
		
		val size = state.transactions.size.takeIf { it > 0 } ?: PAGE_SIZE_INITIAL
		refreshTransactions(requestedSize = size, isInitialLoading = false)
	}
	
	init {
		pagingSource.registerInvalidatedCallback(onInvalidatedCallback)
	}
	
	fun onActionConsumed() {
		state = state.copy(action = null)
	}
	
	private fun applyLoadingResult(
		result: PagingSource.LoadResult<Int, FinancialTransaction>,
		replaceTransactions: Boolean = false,
	) {
		when (result) {
			is PagingSource.LoadResult.Error -> {
				state = state.copy(
					transactionsLoadingMoreError = result.throwable
				)
			}
			
			is PagingSource.LoadResult.Invalid -> Unit //No needed because of invalidated callback is registered
			is PagingSource.LoadResult.Page -> {
				nextPageKey = result.nextKey
				
				state = state.copy(
					transactions = if (replaceTransactions) {
						result.data
					} else {
						state.transactions + result.data
					},
				)
			}
		}
	}
	
	fun onInitialLoadingErrorSubmitted() {
		//do initial loading again
		refreshTransactions(requestedSize = PAGE_SIZE_INITIAL, isInitialLoading = true)
	}
	
	fun onTransactionsLoadingMoreErrorSubmitted() {
		onTransactionsLoadingMore()
	}
	
	fun onTransactionsLoadingMore() {
		if (state.initialLoading || state.transactionsLoadingMore) return
		
		val nextPageKey = nextPageKey ?: return
		
		state = state.copy(
			transactionsLoadingMore = true,
			transactionsLoadingMoreError = null,
		)
		
		schedule {
			applyLoadingResult(
				pagingSource.load(
					PagingSource.LoadParams.Append<Int>(
						key = nextPageKey,
						loadSize = PAGE_SIZE_NEXT,
						placeholdersEnabled = false
					)
				)
			)
			state = state.copy(
				transactionsLoadingMore = false,
			)
		}
	}
	
	fun onTransactionEditClicked(id: Long) {
		val transaction = state.transactions.find { it.id == id } ?: return
		state = state.copy(
			action = AccountHistoryState.Action.EditTransaction(
				accountId = transaction.accountId,
				categoryId = transaction.categoryId,
				transactionId = transaction.id,
			)
		)
	}
	
	fun onTransactionDeleteClicked(id: Long) = schedule(
		skipIfBusy = true,
		postDelay = true,
	) {
		financialManager.deleteTransaction(id)
	}
	
	override fun onCleared() {
		super.onCleared()
		pagingSource.unregisterInvalidatedCallback(onInvalidatedCallback)
	}
	
}