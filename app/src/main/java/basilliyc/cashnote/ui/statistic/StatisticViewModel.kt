package basilliyc.cashnote.ui.statistic

import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.backend.manager.FinancialCurrencyRateManager
import basilliyc.cashnote.backend.preferences.StatisticPreferences
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.data.StatisticMonth
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.flowZip
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class StatisticViewModel : BaseViewModel(), StatisticListener {
	
	private val statisticPreferences: StatisticPreferences by inject()
	private val financialCurrencyRateManager: FinancialCurrencyRateManager by inject()
	
	val state = StatisticStateHolder(
		page = StatisticStateHolder.Page.Loading,
		params = StatisticStateHolder.Params(
			selectedPeriod = statisticPreferences.selectedPeriod.value,
			currency = statisticPreferences.currency.value,
			accounts = emptyList(),
		),
	)
	
	init {
		viewModelScope.launch {
			flowZip(
				statisticPreferences.selectedPeriod.flow,
				statisticPreferences.currency.flow,
				statisticPreferences.accountIds.flow,
				financialManager.getAccountListAsFlow(),
			) { selectedPeriod, currency, accountIds, accounts ->
				state.params.copy(
					selectedPeriod = selectedPeriod,
					currency = currency,
					accounts = accounts.filter { it.id in accountIds },
				)
			}.collectLatest {
				state.params = it
				listenForPageUpdates(it)
			}
		}
	}
	
	private suspend fun FinancialCurrency.convertFrom(
		value: Double,
		sourceCurrency: FinancialCurrency,
	): Double {
		val rate = financialCurrencyRateManager.getRate(sourceCurrency, this)
			?: throw Throwable("Can`t get currency rates")
		return value * rate
	}
	
	private var pageUpdatingJob: Job? = null
		set(value) {
			field?.takeIf { it.isActive }?.cancel()
			field = value
		}
	
	private fun listenForPageUpdates(params: StatisticStateHolder.Params) {
		val startDate = Calendar.getInstance().apply {
			timeInMillis = System.currentTimeMillis()
			set(Calendar.DAY_OF_MONTH, 1)
			set(Calendar.HOUR_OF_DAY, 0)
			set(Calendar.MINUTE, 0)
			set(Calendar.SECOND, 0)
			set(Calendar.MILLISECOND, 0)
			add(Calendar.MONTH, params.selectedPeriod.monthCount * -1)
		}.timeInMillis
		
		pageUpdatingJob = viewModelScope.launch {
			flowZip(
				financialManager.getTransactionListByStartDateAsFlow(startDate),
				financialManager.getCategoryListAsFlow(),
			) { transactions, categories ->
				transactions to categories
			}.collectLatest {
				try {
					state.page = createPageData(params, it.second, it.first)
				} catch (t: Throwable) {
					state.page = StatisticStateHolder.Page.LoadingError(t)
				}
			}
		}
	}
	
	private suspend fun createPageData(
		params: StatisticStateHolder.Params,
		categories: List<FinancialCategory>,
		transactions: List<FinancialTransaction>,
	): StatisticStateHolder.Page.Data {
		//transactions were already ordered descending by date
		
		val (period, currency, accounts) = params
		val calendar = Calendar.getInstance().apply {
			timeInMillis = System.currentTimeMillis()
			set(Calendar.DAY_OF_MONTH, 1)
			set(Calendar.HOUR_OF_DAY, 0)
			set(Calendar.MINUTE, 0)
			set(Calendar.SECOND, 0)
			set(Calendar.MILLISECOND, 0)
		}
		
		val accountMap = accounts.associateBy { it.id }
		val categoryMap = categories.associateBy { it.id }

		
		val values = StatisticValues()
		
		var tIndex = 0 //transactions index
		
		repeat(period.monthCount + 1) {
			val startDate = calendar.timeInMillis
			val month = StatisticMonth(calendar)
			val categoryToValue = HashMap<FinancialCategory, StatisticStateHolder.StatisticValue>()
			if (transactions[tIndex].date >= startDate) {
				while (tIndex < transactions.size && transactions[tIndex].date >= startDate) {
					val transaction = transactions[tIndex++]
					val transactionCurrency =
						accountMap[transaction.accountId]?.currency ?: continue
					val transactionCategory = categoryMap[transaction.categoryId]!!
					
					val value = categoryToValue[transactionCategory]
						?: StatisticStateHolder.StatisticValue()
					
					val transactionValue =
						currency.convertFrom(transaction.value, transactionCurrency)
					if (transactionValue > 0) {
						categoryToValue[transactionCategory] = value.copy(
							income = value.income + transactionValue
						)
					} else {
						categoryToValue[transactionCategory] = value.copy(
							expense = value.expense + transactionValue
						)
					}
				}
			}
			
			values[month] = categoryToValue
			calendar.add(Calendar.MONTH, -1)
		}

		val totalBalance = accounts.sumOf {
			currency.convertFrom(it.balance, it.currency)
		}
		
		return StatisticStateHolder.Page.Data(
			totalBalance = totalBalance,
			values = values,
		)
	}
	
	override fun onResultHandled() {
		state.clearResult()
	}
	
	override fun onParamsClicked() {
		schedule {
			state.result = StatisticStateHolder.Result.NavigateStatisticParams
		}
	}
}