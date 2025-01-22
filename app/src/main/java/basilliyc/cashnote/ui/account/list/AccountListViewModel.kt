package basilliyc.cashnote.ui.account.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.backend.manager.AccountManager
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountListViewModel : BaseViewModel() {
	
	private val accountManager: AccountManager by inject()
	
	private val accountsList = accountManager.getAccountsListAsFlow()
	
	var state: AccountListState.Page by mutableStateOf(AccountListState.Page())
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