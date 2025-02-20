package basilliyc.cashnote.ui.statistic

import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.ui.base.BaseViewModel

class StatisticViewModel : BaseViewModel(), StatisticListener {
	
	
	val state = StatisticStateHolder(
		page = StatisticStateHolder.Page.Loading,
		params = StatisticStateHolder.Params(//TODO rewrite test params with real data
			showMonthCount = 3,
			showCurrentMonth = true,
			currency = FinancialCurrency.UAH,
			accountIds = emptyList<Long>(),
		),
	)
	
	override fun onResultHandled() {
		state.clearResult()
	}
	
	override fun onParamsClicked() {
		schedule {
			state.result = StatisticStateHolder.Result.NavigateStatisticParams
		}
	}
}