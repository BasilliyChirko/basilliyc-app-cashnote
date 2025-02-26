package basilliyc.cashnote.ui.statistic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.StatisticMonth
import basilliyc.cashnote.data.StatisticSelectedPeriod
import basilliyc.cashnote.ui.statistic.StatisticStateHolder.StatisticValue


typealias StatisticValues = HashMap<StatisticMonth, HashMap<FinancialCategory, StatisticValue>>

class StatisticStateHolder(
	page: Page = Page.Loading,
	params: Params,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page as? Page.Data
		set(value) =
			if (value != null) page = value
			else Unit
	
	var params by mutableStateOf(params)
	
	var result by mutableStateOf<Result?>(null)
	
	fun clearResult() {
		result = null
	}
	
	sealed interface Page {
		data object Loading : Page
		data class LoadingError(val throwable: Throwable) : Page
		data class Data(
			val totalBalance: Double,
			val values: StatisticValues,
		) : Page
	}
	
	data class StatisticValue(
		val income: Double = 0.0,
		val expense: Double = 0.0,
	) {
		val profit: Double
			get() = income + expense
	}
	
	data class Params(
		val selectedPeriod: StatisticSelectedPeriod,
		val currency: FinancialCurrency,
		val accounts: List<FinancialAccount>,
		val categories: List<FinancialCategory>,
	)
	
	sealed interface Result {
	
	}
	
}