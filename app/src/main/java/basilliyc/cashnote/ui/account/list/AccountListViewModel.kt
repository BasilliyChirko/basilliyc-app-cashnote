package basilliyc.cashnote.ui.account.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.castOrNull
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountListViewModel : BaseViewModel() {
	
	
	private val financialManager: FinancialManager by inject()
	
	var state: AccountListState by mutableStateOf(AccountListState())
		private set
	
	var draggedList by mutableStateOf<List<FinancialAccount>?>(null)
		private set
	
	init {
		state = state.copy(content = AccountListState.Content.Loading)
		viewModelScope.launch {
			financialManager.getAccountsListAsFlow().collectLatest {
				state = state.copy(
					content = if (it.isNotEmpty()) AccountListState.Content.Data(it)
					else AccountListState.Content.DataEmpty
				)
				draggedList = null
			}
		}
	}
	
	fun onDragStarted() {
		val data = state.content.castOrNull<AccountListState.Content.Data>() ?: return
		draggedList = data.financialAccounts
	}
	
	fun onDragCompleted(from: Int, to: Int) {
		val data = state.content.castOrNull<AccountListState.Content.Data>() ?: return
		draggedList = ArrayList(data.financialAccounts).reordered(from, to)
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