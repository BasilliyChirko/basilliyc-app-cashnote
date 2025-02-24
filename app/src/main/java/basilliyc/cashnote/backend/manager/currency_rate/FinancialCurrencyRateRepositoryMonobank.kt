package basilliyc.cashnote.backend.manager.currency_rate

import retrofit2.http.GET

//https://api.monobank.ua/bank/currency

interface FinancialCurrencyRateRepositoryMonobank {
	
	@GET("bank/currency")
	suspend fun getExchangeRates(): List<MonobankExchangeRate>
	
}

data class MonobankExchangeRate(
	val currencyCodeA: Int,
	val currencyCodeB: Int,
	val rateBuy: Double?,
	val rateSell: Double?,
	val rateCross: Double?,
)


