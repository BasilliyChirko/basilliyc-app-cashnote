package basilliyc.cashnote.backend.preferences

import basilliyc.cashnote.backend.preferences.base.BasePreferences
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.StatisticSelectedPeriod

class StatisticPreferences : BasePreferences() {
	
	val currency = enum("currency", FinancialCurrency.UAH)
	
	val accountIds = longList("accountIds", emptyList())
	
	val categoryIds = longList("categoryIds", emptyList())
	
	val selectedPeriod = enum("selectedPeriod", StatisticSelectedPeriod.ThreeMonths)
	
}