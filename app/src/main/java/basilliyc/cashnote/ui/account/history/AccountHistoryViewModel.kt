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
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.launch

class AccountHistoryViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	companion object {
		const val PAGE_SIZE_INITIAL = 40 //TODO increase to 100
		const val PAGE_SIZE_NEXT = 20 //TODO increase to 50
	}
	
	private val financialManager by inject<FinancialManager>()
	
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
			initialLoading(PAGE_SIZE_INITIAL)
		}
	}
	
	private fun initialLoading(requestedSize: Int) {
		state = state.copy(
			initialLoading = true,
			transactions = emptyList(),
			transactionsLoadingMoreError = null,
		)
		scheduleEvent {
			val initialResult = pagingSource.load(
				params = PagingSource.LoadParams.Refresh<Int>(
					key = null,
					loadSize = requestedSize,
					placeholdersEnabled = false,
				)
			)
			
			if (initialResult is PagingSource.LoadResult.Error) {
				state = state.copy(
					initialLoadingError = initialResult.throwable
				)
			} else {
				applyLoadingResult(initialResult)
			}
			
			state = state.copy(
				initialLoading = false
			)
		}
	}
	
	private val onInvalidatedCallback = {
		pagingSource = financialManager.getTransactionListPagingSource(route.accountId)
		val size = state.transactions.size.takeIf { it > 0 } ?: PAGE_SIZE_INITIAL
		initialLoading(size)
	}
	
	init {
		pagingSource.registerInvalidatedCallback(onInvalidatedCallback)
	}
	
	private fun applyLoadingResult(result: PagingSource.LoadResult<Int, FinancialTransaction>) {
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
					transactions = state.transactions + result.data,
				)
			}
		}
	}
	
	fun onInitialLoadingErrorSubmitted() {
		//do initial loading again
		initialLoading(PAGE_SIZE_INITIAL)
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
		
		scheduleEvent {
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
	
	fun onTransactionClicked(id: Long) {
		//TODO implement, show transaction edit screen
	}
	
	override fun onCleared() {
		super.onCleared()
		pagingSource.unregisterInvalidatedCallback(onInvalidatedCallback)
	}
	
}