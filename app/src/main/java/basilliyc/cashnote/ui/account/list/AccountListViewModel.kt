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
	
	private var stateContentData
		get() = state.content as? AccountListState.Content.Data
		set(value) {
			if (value != null) state = state.copy(content = value)
		}
	
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
	
	fun onActionConsumed() {
		state = state.copy(action = null)
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
	
	
	fun onAccountEditClicked(accountId: Long) = schedule {
		state = state.copy(
			action = AccountListState.Action.AccountEdit(accountId)
		)
	}
	
	fun onAccountDeleteClicked(accountId: Long) {
		state = state.copy(
			dialog = AccountListState.Dialog.AccountDeleteConfirmation(accountId)
		)
	}
	
	fun onAccountHistoryClicked(accountId: Long) = schedule {
		state = state.copy(
			action = AccountListState.Action.AccountHistory(accountId)
		)
	}
	
	fun onAccountDeleteDialogCanceled() {
		if (state.dialog !is AccountListState.Dialog.AccountDeleteConfirmation) return
		state = state.copy(
			dialog = null
		)
	}
	
	fun onAccountDeleteDialogConfirmed(accountId: Long) {
		if (state.dialog !is AccountListState.Dialog.AccountDeleteConfirmation) return
		state = state.copy(
			dialog = null
		)
		
		schedule {
			try {
				financialManager.deleteAccount(accountId)
				state = state.copy(
					action = AccountListState.Action.AccountDeletionSuccess
				)
			} catch (t: Throwable) {
				logcat.error(t)
				state = state.copy(
					action = AccountListState.Action.AccountDeletionError
				)
			}
		}
	}
	
}