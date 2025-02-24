package basilliyc.cashnote.backend.manager.currency_rate

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//http://data.fixer.io/api

interface FinancialCurrencyRateRepositoryFixer {
	
	@GET("{date}")
	suspend fun getExchangeRates(
		@Path("date") date: String,
		@Query("access_key") accessKey: String,
		@Query("symbols") symbols: String = "USD,UAH,EUR",
	): FixerResponse
	
}

data class FixerResponse(
	val base: String,
	val rates: FixerExchangeRate,
)

data class FixerExchangeRate(
	@SerializedName("USD") val usd: Double,
	@SerializedName("UAH") val uah: Double,
	@SerializedName("EUR") val eur: Double,
)


