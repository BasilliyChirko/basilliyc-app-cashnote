package basilliyc.cashnote.ui.account.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialStatistic
import basilliyc.cashnote.data.FinancialStatisticParams
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
	lateinit var account: FinancialAccount
	lateinit var categories: List<FinancialCategory>
	lateinit var statistics: List<FinancialStatistic>
	lateinit var statisticsParams: FinancialStatisticParams
	
	init {
		state = state.copy(page = Page.Loading)
		viewModelScope.launch {
			val account = (financialManager.getAccountById(route.accountId)
				?: throw Throwable("Account with id ${route.accountId} not found"))
				.also { account = it }
			
			val categories = financialManager.getCategoryList()
				.also { categories = it }
			
			val statistics = financialManager.getStatisticsListForAccount(route.accountId)
				.also { statistics = it }
			
			val statisticsParams = financialManager.getStatisticParams()
				.also { statisticsParams = it }
			
			state = state.copy(
				page = Page.Data(
					account = account,
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
			)
			
			initLatestUpdates()
		}
	}
	
	private fun initLatestUpdates() {
		//ACCOUNT
		viewModelScope.launch {
			financialManager.getAccountByIdAsFlow(route.accountId).collectLatest { account ->
				if (account == null) return@collectLatest
				statePageData = statePageData?.copy(
					account = account,
				)
				this@AccountDetailsViewModel.account = account
			}
		}
		
		//CATEGORIES
		viewModelScope.launch {
			financialManager.getCategoryListAsFlow().collectLatest { categories ->
				statePageData = statePageData?.copy(
					categories = categories.map { category ->
						val stats = statistics.find { it.categoryId == category.id }
						AccountDetailsState.CategoryWithBalance(
							category = category,
							primaryValue = stats?.let { it.primaryValuePositive + it.primaryValueNegative }
								?: 0.0,
							secondaryValue = stats?.let { it.secondaryValuePositive + it.secondaryValueNegative }
						)
					}
				)
				this@AccountDetailsViewModel.categories = categories
			}
		}
		
		//STATISTIC PARAMS
		viewModelScope.launch {
			financialManager.getStatisticsParamsAsFlow()
				.collectLatest { statisticsParams ->
					statePageData = statePageData?.copy(
						statisticParams = statisticsParams,
					)
					this@AccountDetailsViewModel.statisticsParams = statisticsParams
				}
		}
		
		//STATISTIC VALUES
		viewModelScope.launch {
			financialManager.getStatisticsListForAccountAsFlow(route.accountId)
				.collectLatest { statistics ->
					statePageData = statePageData?.copy(
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
						}
					)
					this@AccountDetailsViewModel.statistics = statistics
				}
		}
		
		
		
		
	}
}