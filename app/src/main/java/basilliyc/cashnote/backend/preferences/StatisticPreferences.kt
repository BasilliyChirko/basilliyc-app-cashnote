package basilliyc.cashnote.backend.preferences

import basilliyc.cashnote.backend.preferences.base.BasePreferences
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.StatisticSelectedPeriod

class StatisticPreferences : BasePreferences() {
	
	val currency = enum("currency", FinancialCurrency.UAH)
	
	val accountIds = longList("accountIds", emptyList())
	
	val selectedPeriod = enum("selectedPeriod", StatisticSelectedPeriod.ThreeMonths)

//	val showMonthCount = int("showMonthCount", 3) //Zero means all

//	val showCurrentMonth = boolean("showCurrentMonth", true)

	
}