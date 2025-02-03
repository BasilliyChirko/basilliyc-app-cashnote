package basilliyc.cashnote.ui.account.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.ui.account.details.AccountDetailsState.Page
import basilliyc.cashnote.ui.activity.AppNavigation
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountDetailsViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	val route: AppNavigation.AccountDetails = savedStateHandle.toRoute()
	
	var state by mutableStateOf(AccountDetailsState())
		private set
	
	private var statePageData
		get() = state.page as? Page.Data
		set(value) {
			if (value != null) state = state.copy(page = value)
		}
	
	private val financialManager: FinancialManager by inject()
	
	init {
		state = state.copy(page = Page.Loading)
		viewModelScope.launch {
			val account = financialManager.getAccountById(route.accountId)
				?: throw Throwable("Account with id ${route.accountId} not found")
			val categories = financialManager.getCategoryList()
			state = state.copy(
				page = Page.Data(
					account = account,
					showBalanceProfit = true,
					balanceSpend = 0.0,
					balanceReceive = 0.0,
					categories = categories.map {
						AccountDetailsState.CategoryWithBalance(
							category = it,
							balance = 0.0,
							deviation = 0.0,
						)
					}
				)
			)
		}
		
		viewModelScope.launch {
			financialManager.getCategoryListAsFlow().collectLatest { categories ->
				statePageData = statePageData?.copy(
					categories = categories.map {
						AccountDetailsState.CategoryWithBalance(
							category = it,
							balance = 0.0,
							deviation = 0.0,
						)
					}
				)
			}
		}
	}
}