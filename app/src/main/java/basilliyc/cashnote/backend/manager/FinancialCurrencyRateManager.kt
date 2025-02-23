package basilliyc.cashnote.backend.manager

import basilliyc.cashnote.backend.preferences.FinancialCurrencyRatePreferences
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

data class FinancialCurrencyRates(
	val updatedAt: Long,
	val rates: List<FinancialCurrencyRate>,
)

fun FinancialCurrencyRates?.isValid(): Boolean {
	if (this == null) return false
	return updatedAt + 1000 * 60 * 10 > System.currentTimeMillis()
}

data class FinancialCurrencyRate(
	val from: FinancialCurrency,
	val to: FinancialCurrency,
	val rate: Double,
)

class FinancialCurrencyRateManager {
	
	private val ratePreferences: FinancialCurrencyRatePreferences by inject()
	
	suspend fun getRates(): FinancialCurrencyRates? {
		ratePreferences.rates.get()?.takeIf { it.isValid() }?.let { return it }
		
		val rates = FinancialCurrencyRates(
			System.currentTimeMillis(),
			requestRateSource()
		)
		ratePreferences.rates.set(rates)
		return rates
	}
	
	suspend fun getRate(from: FinancialCurrency, to: FinancialCurrency): Double? {
		if (from == to) return 1.0
		return getRates()?.rates?.find {
			it.from == from && it.to == to
		}?.rate
	}
	
	
	private val rateSource: FinancialCurrencyRateRepositoryMonobank by inject()
	private val previousResults = AtomicReference<List<FinancialCurrencyRate>>()
	private val previousResultsDate = AtomicLong(0L)
	private val requestsMutex = Mutex()
	private suspend fun requestRateSource(): List<FinancialCurrencyRate> {
		requestsMutex.withLock {
			// Results valid for 5 minutes
			if (previousResultsDate.get() + 1000 * 60 * 5 > System.currentTimeMillis()) {
				previousResults.get()?.let { return it }
			}
			
			val currencyCodes = FinancialCurrency.entries.map { it.code }
			
			val bankRates = try {
				rateSource.getExchangeRates()
			} catch (e: HttpException) {
				if (e.code() == 429) { //To many requests
					delay(31000)
					rateSource.getExchangeRates()
				} else throw e
			}.filter { it.currencyCodeA in currencyCodes && it.currencyCodeB in currencyCodes }
			
			
			val rates = FinancialCurrency.entries.map { from ->
				FinancialCurrency.entries.mapNotNull { to ->
					if (from == to) return@mapNotNull FinancialCurrencyRate(from, to, 1.0)
					
					//found ordinal rates
					bankRates.firstOrNull {
						it.currencyCodeA == from.code && it.currencyCodeB == to.code
					}?.let {
						if (it.rateSell != null)
							return@mapNotNull FinancialCurrencyRate(from, to, it.rateSell)
						if (it.rateBuy != null)
							return@mapNotNull FinancialCurrencyRate(from, to, 1 / it.rateBuy)
						if (it.rateCross != null)
							return@mapNotNull FinancialCurrencyRate(from, to, it.rateCross)
					}
					
					//found reverse rates
					bankRates.firstOrNull {
						it.currencyCodeA == to.code && it.currencyCodeB == from.code
					}?.let {
						if (it.rateBuy != null)
							return@mapNotNull FinancialCurrencyRate(from, to, 1 / it.rateBuy)
						if (it.rateSell != null)
							return@mapNotNull FinancialCurrencyRate(from, to, it.rateSell)
						if (it.rateCross != null)
							return@mapNotNull FinancialCurrencyRate(from, to, it.rateCross)
					}
					
					null
				}
			}.flatMap { it }
			
			return rates.also {
				previousResults.set(it)
				previousResultsDate.set(System.currentTimeMillis())
			}
			
		}
	}
	
	
}