package basilliyc.cashnote.ui.account.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.ui.account.details.AccountDetailsState.Page
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.flowZip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountDetailsViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), AccountDetailsListener {
	
	val route: AppNavigation.AccountDetails = savedStateHandle.toRoute()
	
	var state by mutableStateOf(AccountDetailsState(showBackButton = !route.isFromNavigation))
		private set
	
	private var statePageData
		get() = state.page as? Page.Data
		set(value) {
			if (value != null) state = state.copy(page = value)
		}
	
	private var stateDialog
		get() = state.dialog
		set(value) {
			state = state.copy(dialog = value)
		}
	
	private var stateResult
		get() = state.result
		set(value) {
			state = state.copy(result = value)
		}
	
	init {
		state = state.copy(page = Page.Loading)
		viewModelScope.launch {
			flowZip(
				financialManager.getAccountByIdAsFlow(route.accountId),
				financialManager.getCategoryListVisibleInAccountAsFlow(route.accountId),
				financialManager.getStatisticListForAccountAsFlow(route.accountId),
				financialManager.getStatisticParamsAsFlow(),
			) { account, categories, statistics, statisticsParams ->
				Page.Data(
					account = account!!,
					balancePrimaryPositive = statistics.sumOf { it.primaryValuePositive },
					balancePrimaryNegative = statistics.sumOf { it.primaryValueNegative },
					balanceSecondaryPositive = statistics.sumOf { it.secondaryValuePositive },
					balanceSecondaryNegative = statistics.sumOf { it.secondaryValueNegative },
					categories = categories.map { category ->
						val stats = statistics.find { it.categoryId == category.id }
						AccountDetailsState.CategoryWithBalance(
							category = category,
							primaryValue = stats?.let { it.primaryValuePositive + it.primaryValueNegative }
								?: 0.0,
							secondaryValue = stats?.let { it.secondaryValuePositive + it.secondaryValueNegative }
						)
					},
					statisticParams = statisticsParams,
				)
			}.collectLatest {
				state = state.copy(
					page = it,
				)
			}
		}
	}
	
	override fun onResultConsumed() {
		stateResult = null
	}
	
	override fun onCategoryClicked(id: Long) {
		stateResult = AccountDetailsState.Result.NavigateTransactionForm(
			accountId = route.accountId,
			categoryId = id,
		)
	}
	
	override fun onAccountCategoriesClicked() {
		stateResult = AccountDetailsState.Result.NavigateCategoryList
	}
	
	override fun onAccountEditClicked() {
		stateResult = AccountDetailsState.Result.NavigateAccountForm(
			accountId = route.accountId
		)
	}
	
	override fun onAccountHistoryClicked() {
		stateResult = AccountDetailsState.Result.NavigateAccountHistory(
			accountId = route.accountId
		)
	}
	
	override fun onAccountDeleteClicked() {
		stateDialog = AccountDetailsState.Dialog.AccountDeleteConfirmation
	}
	
	override fun onDeleteAccountConfirmed() {
		schedule {
			try {
				stateDialog = null
				financialManager.deleteAccount(route.accountId)
				stateResult = AccountDetailsState.Result.AccountDeletionSuccess
			} catch (t: Throwable) {
				stateResult = AccountDetailsState.Result.AccountDeletionError
			}
		}
	}
	
	override fun onDeleteAccountCanceled() {
		stateDialog = null
	}
	
	
}