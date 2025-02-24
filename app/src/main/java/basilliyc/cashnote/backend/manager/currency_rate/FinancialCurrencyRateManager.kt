package basilliyc.cashnote.backend.manager.currency_rate

import basilliyc.cashnote.AppValues
import basilliyc.cashnote.backend.database.FinancialCurrencyRateDao
import basilliyc.cashnote.backend.preferences.FinancialCurrencyRatePreferences
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.FinancialCurrency.*
import basilliyc.cashnote.data.FinancialCurrencyRate
import basilliyc.cashnote.utils.CalendarInstance
import basilliyc.cashnote.utils.Logcat
import basilliyc.cashnote.utils.formatTime
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.moveToFirstDayOfMonth
import basilliyc.cashnote.utils.tryOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

data class FinancialCurrencyRates(
	val updatedAt: Long,
	val rates: List<FinancialCurrencyRateOld>,
)

fun FinancialCurrencyRates?.isValid(): Boolean {
	if (this == null) return false
	return updatedAt + 1000 * 60 * 10 > System.currentTimeMillis()
}

data class FinancialCurrencyRateOld(
	val from: FinancialCurrency,
	val to: FinancialCurrency,
	val rate: Double,
)

class FinancialCurrencyRateManager {
	
	@Suppress("unused")
	private val logcat = Logcat()
	
	private val ratePreferences: FinancialCurrencyRatePreferences by inject()
	private val financialCurrencyRateDao: FinancialCurrencyRateDao by inject()
	
	suspend fun getRates(): FinancialCurrencyRates? {
		ratePreferences.rates.get()?.takeIf { it.isValid() }?.let { return it }
		
		val rates = FinancialCurrencyRates(
			System.currentTimeMillis(),
			requestMonobankRate()
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
	
	
	private val monobankRateRepository: FinancialCurrencyRateRepositoryMonobank by inject()
	private val fixerRateRepository: FinancialCurrencyRateRepositoryFixer by inject()
	private val previousResults = AtomicReference<List<FinancialCurrencyRateOld>>()
	private val previousResultsDate = AtomicLong(0L)
	private val requestsMutex = Mutex()
	private suspend fun requestMonobankRate(): List<FinancialCurrencyRateOld> {
		requestsMutex.withLock {
			// Results valid for 5 minutes
			if (previousResultsDate.get() + 1000 * 60 * 5 > System.currentTimeMillis()) {
				previousResults.get()?.let { return it }
			}
			
			val currencyCodes = FinancialCurrency.entries.map { it.code }
			
			val bankRates = try {
				monobankRateRepository.getExchangeRates()
			} catch (e: HttpException) {
				if (e.code() == 429) { //To many requests
					delay(31000)
					monobankRateRepository.getExchangeRates()
				} else throw e
			}.filter { it.currencyCodeA in currencyCodes && it.currencyCodeB in currencyCodes }
			
			
			val rates = FinancialCurrency.entries.map { from ->
				FinancialCurrency.entries.mapNotNull { to ->
					if (from == to) return@mapNotNull FinancialCurrencyRateOld(from, to, 1.0)
					
					//found ordinal rates
					bankRates.firstOrNull {
						it.currencyCodeA == from.code && it.currencyCodeB == to.code
					}?.let {
						if (it.rateSell != null)
							return@mapNotNull FinancialCurrencyRateOld(from, to, it.rateSell)
						if (it.rateBuy != null)
							return@mapNotNull FinancialCurrencyRateOld(from, to, 1 / it.rateBuy)
						if (it.rateCross != null)
							return@mapNotNull FinancialCurrencyRateOld(from, to, it.rateCross)
					}
					
					//found reverse rates
					bankRates.firstOrNull {
						it.currencyCodeA == to.code && it.currencyCodeB == from.code
					}?.let {
						if (it.rateBuy != null)
							return@mapNotNull FinancialCurrencyRateOld(from, to, 1 / it.rateBuy)
						if (it.rateSell != null)
							return@mapNotNull FinancialCurrencyRateOld(from, to, it.rateSell)
						if (it.rateCross != null)
							return@mapNotNull FinancialCurrencyRateOld(from, to, it.rateCross)
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
	
	
	//Monobank provide free currency rates only for current moment
	private suspend fun requestMonobankRate(date: Long): List<FinancialCurrencyRate> {
		val currencyCodes = FinancialCurrency.entries.map { it.code }
		
		val bankRates = monobankRateRepository.getExchangeRates()
			.filter { it.currencyCodeA in currencyCodes && it.currencyCodeB in currencyCodes }
		
		val rates = FinancialCurrency.entries.map { from ->
			FinancialCurrency.entries.mapNotNull { to ->
				if (from == to) return@mapNotNull FinancialCurrencyRate(date, from, to, 1.0)
				
				//found ordinal rates
				bankRates.firstOrNull {
					it.currencyCodeA == from.code && it.currencyCodeB == to.code
				}?.let {
					if (it.rateSell != null)
						return@mapNotNull FinancialCurrencyRate(date, from, to, it.rateSell)
					if (it.rateBuy != null)
						return@mapNotNull FinancialCurrencyRate(date, from, to, 1 / it.rateBuy)
					if (it.rateCross != null)
						return@mapNotNull FinancialCurrencyRate(date, from, to, it.rateCross)
				}
				
				//found reverse rates
				bankRates.firstOrNull {
					it.currencyCodeA == to.code && it.currencyCodeB == from.code
				}?.let {
					if (it.rateBuy != null)
						return@mapNotNull FinancialCurrencyRate(date, from, to, 1 / it.rateBuy)
					if (it.rateSell != null)
						return@mapNotNull FinancialCurrencyRate(date, from, to, it.rateSell)
					if (it.rateCross != null)
						return@mapNotNull FinancialCurrencyRate(date, from, to, it.rateCross)
				}
				
				null
			}
		}.flatMap { it }
		
		return rates
	}
	
	private suspend fun requestFixerRate(date: Long): List<FinancialCurrencyRate> {
		
		val fixerResponse = fixerRateRepository.getExchangeRates(
			date = date.formatTime("YYYY-MM-DD"),
			symbols = FinancialCurrency.entries.map { it.name }.joinToString(","),
			accessKey = AppValues.FIXER_API_KEY,
		)
		
		val base = FinancialCurrency.valueOf(fixerResponse.base.uppercase())
		
		val bankRates = mapOf(
			USD to fixerResponse.rates.usd,
			UAH to fixerResponse.rates.uah,
			EUR to fixerResponse.rates.eur,
		)
		
		val rates = HashSet<FinancialCurrencyRate>()
		
		//Add rates to same currency
		FinancialCurrency.entries.forEach {
			rates.add(FinancialCurrencyRate(date, base, base, 1.0))
		}
		
		//Add rates to base currency
		FinancialCurrency.entries.forEach {
			rates.add(FinancialCurrencyRate(date, base, it, bankRates[it]!!))
		}
		
		//Add reverse rates to base currency
		FinancialCurrency.entries.forEach {
			rates.add(FinancialCurrencyRate(date, it, base, 1 / bankRates[it]!!))
		}
		
		//Add cross rates
		FinancialCurrency.entries.forEach { from ->
			FinancialCurrency.entries.forEach { to ->
				if (from == to) return@forEach
				if (rates.any { it.from == from && it.to == to }) return@forEach
				val toBase = rates.find { it.from == from && it.to == base } ?: return@forEach
				val fromBase = rates.find { it.from == base && it.to == to } ?: return@forEach
				rates.add(FinancialCurrencyRate(date, from, to, toBase.rate * fromBase.rate))
			}
		}
		
		return rates.toList()
	}
	
	suspend fun getRate(from: FinancialCurrency, to: FinancialCurrency, date: Long): Double? {
		if (from == to) return 1.0
		requestsMutex.withLock {
			val date = CalendarInstance(date).moveToFirstDayOfMonth().timeInMillis
			
			//Check is saved rate existed
			financialCurrencyRateDao.getRate(date, from, to)?.let { return it.rate }
			
			//Can use monobank API for current month
			if (date == CalendarInstance().moveToFirstDayOfMonth().timeInMillis) {
				tryOrNull { requestMonobankRate(date) }?.let {
					financialCurrencyRateDao.saveRates(it)
					//Check is saved rate existed after update
					financialCurrencyRateDao.getRate(date, from, to)?.let { return it.rate }
				}
			}
			
			//If monobank not available or date is not current request fixer
			tryOrNull { requestFixerRate(date) }?.let {
				financialCurrencyRateDao.saveRates(it)
				//Check is saved rate existed after update
				financialCurrencyRateDao.getRate(date, from, to)?.let { return it.rate }
			}
			
			return null
		}
	}
	
	fun test() = CoroutineScope(Dispatchers.IO).launch {
//		financialCurrencyRateDao.deleteAll()
//		logcat.debug("EUR -> UAH", getRate(EUR, UAH, System.currentTimeMillis()))
//		logcat.debug("EUR -> USD", getRate(EUR, USD, System.currentTimeMillis()))
//		logcat.debug("EUR -> EUR", getRate(EUR, EUR, System.currentTimeMillis()))
//		logcat.debug("USD -> UAH", getRate(USD, UAH, System.currentTimeMillis()))
//		logcat.debug("USD -> EUR", getRate(USD, EUR, System.currentTimeMillis()))
//		logcat.debug("USD -> USD", getRate(USD, USD, System.currentTimeMillis()))
//		logcat.debug("UAH -> USD", getRate(UAH, USD, System.currentTimeMillis()))
//		logcat.debug("UAH -> EUR", getRate(UAH, EUR, System.currentTimeMillis()))
//		logcat.debug("UAH -> UAH", getRate(UAH, UAH, System.currentTimeMillis()))
	}
	
}