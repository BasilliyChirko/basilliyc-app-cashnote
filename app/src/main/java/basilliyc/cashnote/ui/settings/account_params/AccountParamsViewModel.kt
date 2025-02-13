package basilliyc.cashnote.ui.settings.account_params

import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.data.FinancialStatisticParams.*
import basilliyc.cashnote.data.getAllowedCalculations
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.settings.account_params.AccountParamsStateHolder.Page
import basilliyc.cashnote.utils.anyTry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountParamsViewModel : BaseViewModel(), AccountParamsListener {
	
	init {
		defaultEventPostDelay = false
		defaultEventSkipIfBusy = false
	}
	
	val stateHolder = AccountParamsStateHolder()
	
	init {
		stateHolder.page = Page.Loading
		viewModelScope.launch {
			financialManager.getStatisticParamsAsFlow().collectLatest { statisticParams ->
				stateHolder.page = Page.Data(
					statisticParams = statisticParams,
					allowedCalculations = statisticParams.period.getAllowedCalculations(),
				)
			}
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