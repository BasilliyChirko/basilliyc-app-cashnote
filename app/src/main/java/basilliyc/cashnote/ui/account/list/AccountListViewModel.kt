package basilliyc.cashnote.ui.account.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.castOrNull
import basilliyc.cashnote.utils.flowZip
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.launch

class AccountListViewModel : BaseViewModel() {
	
	var state: AccountListState by mutableStateOf(AccountListState())
		private set
	
	var draggedList by mutableStateOf<List<AccountListState.AccountBalance>?>(null)
		private set
	
	init {
		state = state.copy(content = AccountListState.Content.Loading)
		viewModelScope.launch {
			
			flowZip(
				financialManager.getAccountsListAsFlow(),
				financialManager.getStatisticsListAsFlow()
			) { accounts, statistics ->
				state = state.copy(
					content = if (accounts.isNotEmpty()) {
						val data = accounts.map { account ->
							val primaryValue = statistics.filter { it.accountId == account.id }
								.sumOf { it.primaryValuePositive + it.primaryValueNegative }
								.takeIf { it != 0.0 }
							AccountListState.AccountBalance(
								account = account,
								primaryValue = primaryValue
							)
						}
						AccountListState.Content.Data(data)
					} else AccountListState.Content.DataEmpty
				)
				draggedList = null
			}
			
			
//			financialManager.getAccountsListAsFlow().collectLatest {
//				state = state.copy(
//					content = if (it.isNotEmpty()) AccountListState.Content.Data(it)
//					else AccountListState.Content.DataEmpty
//				)
//				draggedList = null
//			}
		}
	}
	
	fun onDragStarted() {
		val data = state.content.castOrNull<AccountListState.Content.Data>() ?: return
		draggedList = data.accounts
	}
	
	fun onDragCompleted(from: Int, to: Int) {
		val data = state.content.castOrNull<AccountListState.Content.Data>() ?: return
		draggedList = ArrayList(data.accounts).reordered(from, to)
		viewModelScope.launch {
			financialManager.changeAccountPosition(from, to)
		}
	}
	
	fun onDragReverted() {
		draggedList = null
	}
	
	fun onDragMoved(from: Int, to: Int) {
		draggedList = draggedList?.let { ArrayList(it) }?.reordered(from, to)
	}
	
	
}