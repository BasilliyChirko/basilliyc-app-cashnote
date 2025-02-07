package basilliyc.cashnote.ui.account.params

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.data.FinancialStatisticParams.*
import basilliyc.cashnote.data.getAllowedCalculations
import basilliyc.cashnote.ui.account.params.AccountParamsStateHolder.Page
import basilliyc.cashnote.ui.activity.AppNavigation
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.anyTry
import kotlinx.coroutines.launch

class AccountParamsViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), AccountParamsListener {
	
	init {
		defaultEventPostDelay = false
		defaultEventSkipIfBusy = false
	}
	
	val route: AppNavigation.AccountParams = savedStateHandle.toRoute()
	
	/*	var state by mutableStateOf(AccountParamsState())
			private set
		
		private var statePageData
			get() = state.pageData
			set(value) {
				if (value != null) state = state.copy(page = value)
			}
		
	//	private var stateDialog
	//		get() = state.dialog
	//		set(value) {
	//			state = state.copy(dialog = value)
	//		}
		
		private var stateResult
			get() = state.result
			set(value) {
				state = state.copy(result = value)
			}*/
	
	val stateHolder = AccountParamsStateHolder()
	
	init {
		stateHolder.page = Page.Loading
		viewModelScope.launch {
//			val account = financialManager.requireAccountById(route.accountId)
			
			val statisticParams = financialManager.getStatisticParams()
			
			val allowedCalculations = statisticParams.period.getAllowedCalculations()
			
			stateHolder.page = Page.Data(
				statisticParams = statisticParams,
				allowedCalculations = allowedCalculations,
			)
		}
	}
	
	override fun onStatisticPeriodChanged(period: Period) {
		val allowedCalculations = period.getAllowedCalculations()
		
		val currentParams = stateHolder.pageDataStatisticParams ?: return
		
		val params = currentParams.copy(
			period = period,
			primaryValueCalculation = currentParams.primaryValueCalculation.takeIf { it in allowedCalculations }
				?: allowedCalculations.first(),
			secondaryValueCalculation = currentParams.secondaryValueCalculation.takeIf { it in allowedCalculations }
				?: allowedCalculations.first(),
		)
		
		schedule {
			anyTry {
				financialManager.saveStatisticParams(params)
				stateHolder.pageData = stateHolder.pageData?.copy(
					statisticParams = params,
					allowedCalculations = allowedCalculations,
				)
			}
		}
	}
	
	override fun onStatisticPrimaryValueCalculationChanged(calculation: Calculation) {
		val params = stateHolder.pageDataStatisticParams?.copy(
			primaryValueCalculation = calculation
		) ?: return
		
		schedule {
			anyTry {
				financialManager.saveStatisticParams(params)
				stateHolder.pageDataStatisticParams = params
			}
		}
	}
	
	override fun onStatisticSecondaryValueCalculationChanged(calculation: Calculation) {
		val params = stateHolder.pageDataStatisticParams?.copy(
			secondaryValueCalculation = calculation
		) ?: return
		
		schedule {
			anyTry {
				financialManager.saveStatisticParams(params)
				stateHolder.pageDataStatisticParams = params
			}
		}
	}
	
	override fun onStatisticShowAccountStatisticChanged(showAccountStatistic: Boolean) {
		val params = stateHolder.pageDataStatisticParams?.copy(
			showAccountStatistic = showAccountStatistic
		) ?: return
		
		schedule {
			anyTry {
				financialManager.saveStatisticParams(params)
				stateHolder.pageDataStatisticParams = params
			}
		}
	}
	
	override fun onStatisticShowSecondaryValueForCategoryChanged(showSecondaryValueForCategory: Boolean) {
		val params = stateHolder.pageDataStatisticParams?.copy(
			showSecondaryValueForCategory = showSecondaryValueForCategory
		) ?: return
		
		schedule {
			anyTry {
				financialManager.saveStatisticParams(params)
				stateHolder.pageDataStatisticParams = params
			}
		}
	}
	
	override fun onStatisticShowSecondaryValueForAccountChanged(showSecondaryValueForAccount: Boolean) {
		val params = stateHolder.pageDataStatisticParams?.copy(
			showSecondaryValueForAccount = showSecondaryValueForAccount
		) ?: return
		
		schedule {
			anyTry {
				financialManager.saveStatisticParams(params)
				stateHolder.pageDataStatisticParams = params
			}
		}
	}
	
}