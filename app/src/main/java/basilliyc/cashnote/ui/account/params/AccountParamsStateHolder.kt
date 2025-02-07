package basilliyc.cashnote.ui.account.params

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.data.FinancialStatisticParams

class AccountParamsStateHolder(
	page: Page = Page.Loading,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page as? Page.Data
		set(value) =
			if (value != null) page = value
			else Unit
	
	var pageDataStatisticParams
		get() = pageData?.statisticParams
		set(value) =
			if (value != null) pageData = pageData?.copy(statisticParams = value)
			else Unit
	
	var pageDataAllowedCalculations
		get() = pageData?.allowedCalculations
		set(value) =
			if (value != null) pageData = pageData?.copy(allowedCalculations = value)
			else Unit
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val statisticParams: FinancialStatisticParams,
			val allowedCalculations: List<FinancialStatisticParams.Calculation>,
		) : Page
	}
	
}
