package basilliyc.cashnote.backend.preferences

import basilliyc.cashnote.backend.manager.currency_rate.FinancialCurrencyRates
import basilliyc.cashnote.backend.preferences.base.BasePreferences
import com.google.gson.GsonBuilder

class FinancialCurrencyRatePreferences : BasePreferences() {
	
	private val gson = GsonBuilder()
		.serializeNulls()
		.create()
	
	val rates = customOrNull("rates",
		convertToString = {
			gson.toJson(it).toString()
		},
		convertFromString = {
			gson.fromJson(it, FinancialCurrencyRates::class.java)
		}
	)
	
}