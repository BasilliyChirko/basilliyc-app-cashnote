package basilliyc.cashnote.ui.statistic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.data.FinancialCurrency

class StatisticStateHolder(
	page: Page = Page.Loading,
	params: Params,
	pageType: PageType = PageType.Balance,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page as? Page.Data
		set(value) =
			if (value != null) page = value
			else Unit
	
	var pageType by mutableStateOf(pageType)
	
	var params by mutableStateOf(params)
	
	var result by mutableStateOf<Result?>(null)
	
	fun clearResult() {
		result = null
	}
	
	sealed interface Page {
		data object Loading : Page
		data object DataEmpty : Page
		data class Data(
			val stub: Int = 0
		) : Page
	}
	
	enum class PageType {
		Balance, Spend, Income
	}
	
	data class Params(
		val showMonthCount: Int,
		val showCurrentMonth: Boolean,
		val currency: FinancialCurrency,
		val accountIds: List<Long>,
	)
	
	sealed interface Result {
		data object NavigateStatisticParams : Result
	}
	
}