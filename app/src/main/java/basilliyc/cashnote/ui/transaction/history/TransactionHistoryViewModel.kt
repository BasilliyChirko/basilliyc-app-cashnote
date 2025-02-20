package basilliyc.cashnote.ui.transaction.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingSource
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.toMap
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), TransactionHistoryListener {
	
	companion object {
		const val PAGE_SIZE_INITIAL = 100
		const val PAGE_SIZE_NEXT = 50
	}
	
	private val route: AppNavigation.TransactionHistory = savedStateHandle.toRoute()

	val state = TransactionHistoryStateHolder(
		page = TransactionHistoryStateHolder.Page(
			showBackButton = !route.isFromNavigation,
		)
	)
	
	private var pagingSource = financialManager.getTransactionListPagingSource(route.accountId)
	private var nextPageKey: Int? = null
	
	//Initial loading account and data
	init {
		state.page = state.page.copy(initialLoading = true)
		viewModelScope.launch {
			val account = route.accountId?.let { financialManager.getAccountById(it) }
			state.page = state.page.copy(
				singleAccount = account,
			)
			refreshTransactions(requestedSize = PAGE_SIZE_INITIAL, isInitialLoading = true)
		}
		viewModelScope.launch {
			financialManager.getAccountListAsFlow().collectLatest {
				state.page = state.page.copy(accounts = it.toMap { it.id to it })
			}
		}
		viewModelScope.launch {
			financialManager.getCategoryListAsFlow().collectLatest {
				state.page = state.page.copy(categories = it.toMap { it.id to it })
			}
		}
	}
	
	private fun refreshTransactions(requestedSize: Int, isInitialLoading: Boolean) {
		state.page = state.page.copy(
			initialLoading = isInitialLoading,
			transactions = if (isInitialLoading) emptyList() else state.page.transactions,
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
				state.page = state.page.copy(
					initialLoadingError = refreshLoadResult.throwable
				)
			} else {
				applyLoadingResult(refreshLoadResult, replaceTransactions = true)
			}
			
			state.page = state.page.copy(
				initialLoading = false
			)
		}
	}
	
	private val onInvalidatedCallback: () -> Unit = {
		pagingSource.unregisterInvalidatedCallback(onInvalidatedCallback)
		pagingSource = financialManager.getTransactionListPagingSource(route.accountId)
		pagingSource.registerInvalidatedCallback(onInvalidatedCallback)
		
		val size = state.page.transactions.size.takeIf { it > 0 } ?: PAGE_SIZE_INITIAL
		refreshTransactions(requestedSize = size, isInitialLoading = false)
	}
	
	init {
		pagingSource.registerInvalidatedCallback(onInvalidatedCallback)
	}
	
	override fun onResultHandled() {
		state.result = null
	}
	
	private fun applyLoadingResult(
		result: PagingSource.LoadResult<Int, FinancialTransaction>,
		replaceTransactions: Boolean = false,
	) {
		when (result) {
			is PagingSource.LoadResult.Error -> {
				state.page = state.page.copy(
					transactionsLoadingMoreError = result.throwable
				)
			}
			
			is PagingSource.LoadResult.Invalid -> Unit //No needed because of invalidated callback is registered
			is PagingSource.LoadResult.Page -> {
				nextPageKey = result.nextKey
				
				state.page = state.page.copy(
					transactions = if (replaceTransactions) {
						result.data
					} else {
						state.page.transactions + result.data
					},
				)
			}
		}
	}
	
	override fun onInitialLoadingErrorSubmitted() {
		//do initial loading again
		refreshTransactions(requestedSize = PAGE_SIZE_INITIAL, isInitialLoading = true)
	}
	
	override fun onTransactionsLoadingMoreErrorSubmitted() {
		onTransactionsLoadingMore()
	}
	
	override fun onTransactionsLoadingMore() {
		if (state.page.initialLoading || state.page.transactionsLoadingMore) return
		
		val nextPageKey = nextPageKey ?: return
		
		state.page = state.page.copy(
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
			state.page = state.page.copy(
				transactionsLoadingMore = false,
			)
		}
	}
	
	override fun onTransactionEditClicked(id: Long) {
		val transaction = state.page.transactions.find { it.id == id } ?: return
		state.result = TransactionHistoryStateHolder.Result.EditTransaction(
			accountId = transaction.accountId,
			categoryId = transaction.categoryId,
			transactionId = transaction.id,
		)
	}
	
	override fun onTransactionDeleteClicked(id: Long) = schedule(
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