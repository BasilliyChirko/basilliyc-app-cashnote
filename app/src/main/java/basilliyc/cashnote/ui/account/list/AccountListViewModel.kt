package basilliyc.cashnote.ui.account.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountListViewModel : BaseViewModel() {
	
	private val financialManager: FinancialManager by inject()
	
	private val accountsList = financialManager.getAccountsListAsFlow()
	
	var state: AccountListState by mutableStateOf(AccountListState())
		private set
	
	init {
		state = state.copy(content = AccountListState.Content.Loading)
		viewModelScope.launch {
			accountsList.collectLatest {
				state = state.copy(
					content = if (it.isNotEmpty()) AccountListState.Content.Data(it)
					else AccountListState.Content.DataEmpty
				)
			}
		}
	}
	
}