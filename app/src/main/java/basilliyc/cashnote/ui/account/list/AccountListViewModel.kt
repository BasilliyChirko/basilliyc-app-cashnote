package basilliyc.cashnote.ui.account.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.flowZip
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.launch

class AccountListViewModel : BaseViewModel(), AccountListListener {
	
	var state: AccountListStateHolder by mutableStateOf(AccountListStateHolder())
		private set
	
	init {
		state.page = AccountListStateHolder.Page.Loading
		viewModelScope.launch {
			flowZip(
				financialManager.getAccountsListAsFlow(),
				financialManager.getStatisticsListAsFlow(),
				preferences.accountListSingleLine.flow,
			) { accounts, statistics, singleLine ->
				val data = accounts.map { account ->
					val primaryValue = statistics.filter { it.accountId == account.id }
						.sumOf { it.primaryValuePositive + it.primaryValueNegative }
						.takeIf { it != 0.0 }
					AccountListStateHolder.AccountBalance(
						account = account,
						primaryValue = primaryValue
					)
				}
				
				state.pageData = AccountListStateHolder.Page.Data(
					accounts = data,
					accountsDragged = null,
					isSingleLine = singleLine,
				)
			}
		}
	}
	
	override fun onResultHandled() {
		state.result = null
	}
	
	override fun onDragStarted() {
		val data = state.pageData ?: return
		state.pageData = data.copy(
			accountsDragged = data.accounts
		)
	}
	
	override fun onDragCompleted(from: Int, to: Int) {
		val data = state.pageData ?: return
		state.pageData = data.copy(
			accountsDragged = ArrayList(data.accounts).reordered(from, to)
		)
		viewModelScope.launch {
			financialManager.changeAccountPosition(from, to)
		}
	}
	
	override fun onDragReverted() {
		val data = state.pageData ?: return
		state.pageData = data.copy(
			accountsDragged = null
		)
	}
	
	override fun onDragMoved(from: Int, to: Int) {
		val data = state.pageData ?: return
		val draggedList = data.accountsDragged ?: return
		state.pageData = data.copy(
			accountsDragged = ArrayList(draggedList).reordered(from, to)
		)
	}
	
	override fun onClickAddNewAccount() {
		state.result = AccountListStateHolder.Result.NavigateAccountForm
	}
	
	override fun onClickAccount(id: Long) {
		state.result = AccountListStateHolder.Result.NavigateAccountDetails(id)
	}
	
}