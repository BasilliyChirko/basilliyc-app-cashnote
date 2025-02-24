package basilliyc.cashnote.backend.manager.currency_rate

//http://data.fixer.io/api

class FinancialCurrencyRateRepositoryFixerTest : FinancialCurrencyRateRepositoryFixer {
	
	val default = FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.036215, uah = 43.270951, eur = 1.0))
	private val testData = mapOf<String, FixerResponse>(
		"2025-02-01" to default,
		"2025-01-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.03, uah = 43.2, eur = 1.0)),
		"2023-01-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0700, uah = 40.23, eur = 1.0)),
		"2023-02-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0800, uah = 40.50, eur = 1.0)),
		"2023-03-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0600, uah = 39.80, eur = 1.0)),
		"2023-04-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0900, uah = 40.70, eur = 1.0)),
		"2023-05-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.1000, uah = 41.00, eur = 1.0)),
		"2023-06-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0700, uah = 40.20, eur = 1.0)),
		"2023-07-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0900, uah = 40.60, eur = 1.0)),
		"2023-08-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.1000, uah = 41.00, eur = 1.0)),
		"2023-09-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0800, uah = 40.50, eur = 1.0)),
		"2023-10-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0600, uah = 39.80, eur = 1.0)),
		"2023-11-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0900, uah = 40.70, eur = 1.0)),
		"2023-12-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.1000, uah = 41.00, eur = 1.0)),
		"2024-01-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.1000, uah = 41.00, eur = 1.0)),
		"2024-02-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0800, uah = 40.50, eur = 1.0)),
		"2024-03-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0900, uah = 40.70, eur = 1.0)),
		"2024-04-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0700, uah = 40.20, eur = 1.0)),
		"2024-05-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0900, uah = 40.60, eur = 1.0)),
		"2024-06-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0800, uah = 40.50, eur = 1.0)),
		"2024-07-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.1000, uah = 41.00, eur = 1.0)),
		"2024-08-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0900, uah = 40.70, eur = 1.0)),
		"2024-09-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0700, uah = 40.20, eur = 1.0)),
		"2024-10-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0800, uah = 40.50, eur = 1.0)),
		"2024-11-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.0900, uah = 40.70, eur = 1.0)),
		"2024-12-01" to FixerResponse(base = "EUR", rates = FixerExchangeRate(usd = 1.1000, uah = 41.00, eur = 1.0)),
	)
	
	override suspend fun getExchangeRates(
		date: String,
		accessKey: String,
		symbols: String,
	): FixerResponse {
		testData[date]?.let { return it }
		return default
	}
}

