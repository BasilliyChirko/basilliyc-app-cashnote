package basilliyc.cashnote.backend.manager

import androidx.room.withTransaction
import basilliyc.cashnote.backend.database.AppDatabase
import basilliyc.cashnote.backend.database.FinancialAccountDao
import basilliyc.cashnote.backend.database.FinancialCategoryDao
import basilliyc.cashnote.backend.database.FinancialStatisticDao
import basilliyc.cashnote.backend.database.FinancialTransactionDao
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialStatistic
import basilliyc.cashnote.data.FinancialStatisticParams
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.utils.CalendarInstance
import basilliyc.cashnote.utils.Logcat
import basilliyc.cashnote.utils.applyIf
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.monthInMillis
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class FinancialManager {
	
	@Suppress("unused")
	private val logcat = Logcat(this)
	
	private val appDatabase: AppDatabase by inject()
	private val accountDao: FinancialAccountDao by inject()
	private val transactionDao: FinancialTransactionDao by inject()
	private val categoryDao: FinancialCategoryDao by inject()
	private val statisticDao: FinancialStatisticDao by inject()
	
	
	private suspend inline fun <T> databaseTransaction(crossinline block: suspend () -> T) {
		appDatabase.withTransaction { block() }
	}
	
	//----------------------------------------------------------------------------------------------
	//  Account
	//----------------------------------------------------------------------------------------------
	
	fun getAccountsListAsFlow() = accountDao.getListAsFlow()
	
	suspend fun getAccountById(id: Long) = accountDao.getById(id)
	
	fun getAccountByIdAsFlow(id: Long) = accountDao.getByIdAsFlow(id)
	
	suspend fun saveAccount(financialAccount: FinancialAccount) = databaseTransaction {
		val financialAccount = financialAccount
			.applyIf({ id == 0L }) {
				copy(
					position = accountDao.getNextPosition()
				)
			}
		
		accountDao.save(financialAccount)
		
		refreshStatistics(force = true)
	}
	
	suspend fun deleteAccount(accountId: Long) = databaseTransaction {
		accountDao.delete(accountId)
		
		val accounts = accountDao.getList()
			.mapIndexed { index, category ->
				category.copy(
					position = index
				)
			}
		accountDao.save(accounts)
		
		refreshStatistics(force = true)
	}
	
	suspend fun changeAccountPosition(from: Int, to: Int) = databaseTransaction {
		if (from == to) return@databaseTransaction
		if (from == -1 || to == -1) return@databaseTransaction
		val accounts = accountDao.getList()
			.reordered(from, to)
			.mapIndexed { index, category ->
				category.copy(
					position = index
				)
			}
		
		accountDao.save(accounts)
	}
	
	private suspend fun FinancialAccountDao.getNextPosition(): Int {
		var currentMaxPosition = this.getMaxPosition()
		
		if (currentMaxPosition == 0) {
			if (this.getItemsCount() == 0) {
				currentMaxPosition = -1
			}
		}
		
		return currentMaxPosition + 1
	}
	
	//----------------------------------------------------------------------------------------------
	//  Category
	//----------------------------------------------------------------------------------------------
	
	suspend fun getCategoryList() = categoryDao.getList()
	
	fun getCategoryListAsFlow() = categoryDao.getListAsFlow()
	
	suspend fun getCategoryById(id: Long) = categoryDao.getById(id)
	
	private suspend fun FinancialCategoryDao.getNextPosition(): Int {
		var currentMaxPosition = this.getMaxPosition()
		
		if (currentMaxPosition == 0) {
			if (this.getItemsCount() == 0) {
				currentMaxPosition = -1
			}
		}
		
		return currentMaxPosition + 1
	}
	
	suspend fun saveCategory(category: FinancialCategory) = databaseTransaction {
		val category = category
			.applyIf({ id == 0L }) {
				copy(
					position = categoryDao.getNextPosition()
				)
			}
		
		categoryDao.save(category)
		
		refreshStatistics(force = true)
	}
	
	suspend fun deleteCategory(categoryId: Long) = databaseTransaction {
		categoryDao.delete(categoryId)
		val categories = categoryDao.getList()
			.mapIndexed { index, category ->
				category.copy(
					position = index
				)
			}
		categoryDao.save(categories)
		
		refreshStatistics(force = true)
	}
	
	suspend fun changeCategoryPosition(from: Int, to: Int) = databaseTransaction {
		if (from == to) return@databaseTransaction
		if (from == -1 || to == -1) return@databaseTransaction
		val categories = categoryDao.getList()
			.reordered(from, to)
			.mapIndexed { index, category ->
				category.copy(
					position = index
				)
			}
		categoryDao.save(categories)
	}
	
	//----------------------------------------------------------------------------------------------
	//  Transaction
	//----------------------------------------------------------------------------------------------
	
	suspend fun saveTransaction(
		transaction: FinancialTransaction,
		isAppend: Boolean = true,
	) {
		
		if (transaction.value == 0.0) return
		
		val account = accountDao.getById(transaction.accountId)
			?: throw IllegalStateException("Can`t create transaction. Account with id ${transaction.accountId} is not present in database")
		
		//Validate is category really exists
		val category = categoryDao.getById(transaction.categoryId)
			?: throw IllegalStateException("Can`t create transaction. Category with id ${transaction.categoryId} is not present in database")
		
		databaseTransaction {
			transactionDao.save(transaction)
			accountDao.save(account.copy(balance = account.balance + transaction.value))
			
			if (isAppend) {
				refreshStatistics(force = false)
				appendStatisticForCurrentPeriod(transaction)
			} else {
				refreshStatistics(force = true)
			}
		}
		
	}
	
	suspend fun getTransactionById(id: Long) = transactionDao.getById(id)
	
	fun getTransactionListPagingSource(accountId: Long) =
		transactionDao.getListPagingSource(accountId)
	
	suspend fun deleteTransaction(transactionId: Long) = databaseTransaction {
		val transaction = transactionDao.getById(transactionId)!!
		val account = accountDao.getById(transaction.accountId)!!
		
		accountDao.save(account.copy(balance = account.balance - transaction.value))
		transactionDao.delete(transactionId)
		
		refreshStatistics(force = true)
	}
	
	//----------------------------------------------------------------------------------------------
	//  Statistic
	//----------------------------------------------------------------------------------------------
	
	suspend fun getStatisticParams() = statisticDao.getParams()
		?: FinancialStatisticParams().also { statisticDao.saveParams(it) }
	
	suspend fun saveStatisticParams(params: FinancialStatisticParams) =
		statisticDao.saveParams(params)
	
	suspend fun updateStatisticCalculationDate() {
		val currentParams = getStatisticParams()
		val periodEnd = PeriodValue(currentParams.period, System.currentTimeMillis()).end
		statisticDao.saveParams(currentParams.copy(calculationValidUntil = periodEnd))
	}
	
	fun getStatisticsParamsAsFlow() = statisticDao.getParamsAsFlow()
		.mapNotNull { it ?: FinancialStatisticParams() }
	
	suspend fun getStatisticsListForAccount(accountId: Long) = statisticDao.getListForAccount(accountId)
	
	fun getStatisticsListForAccountAsFlow(accountId: Long) = statisticDao.getListForAccountAsFlow(accountId)
	
	suspend fun isStatisticValid(): Boolean {
		return System.currentTimeMillis() <= getStatisticParams().calculationValidUntil
	}
	
	suspend fun refreshStatistics(force: Boolean = false) {
		if (!force && isStatisticValid()) return
		
		val date = System.currentTimeMillis()
		val params = getStatisticParams()
		
		statisticDao.deleteAll()
		
		val accounts = accountDao.getList()
		val categories = categoryDao.getList()
		
		val periodValue = PeriodValue(params.period, date)
		
		val statistics = ArrayList<FinancialStatistic>()
		
		accounts.forEach { account ->
			categories.forEach { category ->
				
				val primaryValue =
					params.primaryValueCalculation.getValue(account, category, periodValue)
				
				val secondaryValue =
					params.secondaryValueCalculation.getValue(account, category, periodValue)
				
				statistics.add(
					FinancialStatistic(
						accountId = account.id,
						categoryId = category.id,
						primaryValuePositive = primaryValue.first,
						primaryValueNegative = primaryValue.second,
						secondaryValuePositive = secondaryValue.first,
						secondaryValueNegative = secondaryValue.second,
					)
				)
			}
		}
		
		statisticDao.save(statistics)
		
		updateStatisticCalculationDate()
	}
	
	private suspend fun appendStatisticForCurrentPeriod(transaction: FinancialTransaction) {
		val params = getStatisticParams()
		var statistic = statisticDao.getById(
			accountId = transaction.accountId,
			categoryId = transaction.categoryId,
		) ?: throw IllegalStateException(
			"Can`t update statistic. " +
					"Statistic with accountId=${transaction.accountId} " +
					"and categoryId=${transaction.categoryId} " +
					"is not present in database"
		)
		
		if (params.primaryValueCalculation == FinancialStatisticParams.Calculation.CurrentPeriod) {
			statistic = if (transaction.value >= 0) {
				statistic.copy(
					primaryValuePositive = statistic.primaryValuePositive + transaction.value
				)
			} else {
				statistic.copy(
					primaryValueNegative = statistic.primaryValueNegative + transaction.value
				)
			}
		}
		
		if (params.secondaryValueCalculation == FinancialStatisticParams.Calculation.CurrentPeriod) {
			statistic = if (transaction.value >= 0) {
				statistic.copy(
					secondaryValuePositive = statistic.secondaryValuePositive + transaction.value
				)
			} else {
				statistic.copy(
					secondaryValueNegative = statistic.secondaryValueNegative + transaction.value
				)
			}
		}
		
		statisticDao.save(statistic)
	}
	
	private suspend fun FinancialStatisticParams.Calculation.getValue(
		account: FinancialAccount,
		category: FinancialCategory,
		initialPeriodValue: PeriodValue,
	): Pair<Double, Double> {
		
		var positive = 0.0
		var negative = 0.0
		
		when (this) {
			FinancialStatisticParams.Calculation.CurrentPeriod -> {
				return initialPeriodValue.getValue(account, category)
			}
			
			FinancialStatisticParams.Calculation.PreviousPeriod -> {
				return initialPeriodValue.previous.getValue(account, category)
			}
			
			else -> Unit
		}
		
		var period = initialPeriodValue.previous
		val periodsCount = this.getPeriodsCount(account, category, period)
		repeat(periodsCount) {
			val value = period.getValue(account, category)
			positive += value.first
			negative += value.second
			period = period.previous
		}
		
		positive /= periodsCount
		negative /= periodsCount
		
		return positive to negative
		
	}
	
	private suspend fun FinancialStatisticParams.Calculation.getPeriodsCount(
		account: FinancialAccount,
		category: FinancialCategory,
		periodValue: PeriodValue,
	): Int {
		var count = 0
		val calendarCurrent = CalendarInstance(periodValue.end)
		val calendarTarget = CalendarInstance(periodValue.end)
		
		when (this) {
			FinancialStatisticParams.Calculation.CurrentPeriod -> return 0
			FinancialStatisticParams.Calculation.PreviousPeriod -> return 1
			FinancialStatisticParams.Calculation.AveragePreviousPeriodsForMonth1 -> {
				calendarTarget.add(Calendar.MONTH, -1)
			}
			
			FinancialStatisticParams.Calculation.AveragePreviousPeriodsForMonth3 -> {
				calendarTarget.add(Calendar.MONTH, -3)
			}
			
			FinancialStatisticParams.Calculation.AveragePreviousPeriodsForMonth6 -> {
				calendarTarget.add(Calendar.MONTH, -6)
			}
			
			FinancialStatisticParams.Calculation.AveragePreviousPeriodsForYear -> {
				calendarTarget.add(Calendar.YEAR, -1)
			}
			
			FinancialStatisticParams.Calculation.AveragePreviousPeriodsForAllTime -> {
				calendarTarget.set(Calendar.YEAR, 1970)
			}
		}
		
		calendarTarget.timeInMillis = maxOf(
			calendarTarget.timeInMillis,
			transactionDao.getEarliestTransactionDate(account.id, category.id),
		)
		
		
		while (calendarCurrent > calendarTarget) {
			calendarCurrent.add(periodValue.period, -1)
			count++
		}
		
		return count
	}
	
	private fun Calendar.add(period: FinancialStatisticParams.Period, value: Int) {
		when (period) {
			FinancialStatisticParams.Period.Day -> this.add(Calendar.DAY_OF_MONTH, value)
			FinancialStatisticParams.Period.Month -> this.add(Calendar.MONTH, value)
			FinancialStatisticParams.Period.Year -> this.add(Calendar.YEAR, value)
		}
	}
	
	private suspend fun PeriodValue.getValue(
		account: FinancialAccount,
		category: FinancialCategory,
	): Pair<Double, Double> {
		
		var positive = 0.0
		var negative = 0.0
		
		transactionDao.getListForStatistic(
			accountId = account.id,
			categoryId = category.id,
			periodStart = start,
			periodEnd = end,
		).forEach {
			if (it.value >= 0) positive += it.value
			else negative += it.value
		}
		
		return positive to negative
	}
	
	
	//----------------------------------------------------------------------------------------------
	//  Other
	//----------------------------------------------------------------------------------------------
	
	
	private class PeriodValue(
		val period: FinancialStatisticParams.Period,
		date: Long,
	) {
		
		private fun createEnd(period: FinancialStatisticParams.Period, date: Long): Long {
			val calendar = CalendarInstance(date)
			
			calendar.set(Calendar.SECOND, 59)
			calendar.set(Calendar.MINUTE, 59)
			calendar.set(Calendar.HOUR_OF_DAY, 23)
			if (period == FinancialStatisticParams.Period.Day) return calendar.timeInMillis
			
			calendar.add(Calendar.MONTH, 1)
			calendar.set(Calendar.DAY_OF_MONTH, 0)
			if (period == FinancialStatisticParams.Period.Month) {
				return calendar.timeInMillis
			}
			
			calendar.set(Calendar.MONTH, Calendar.UNDECIMBER)
			if (period == FinancialStatisticParams.Period.Year) return calendar.timeInMillis
			
			return calendar.timeInMillis
		}
		
		val end: Long by lazy {
			createEnd(period, date = date)
		}
		
		val start: Long by lazy {
			val calendar = CalendarInstance(end)
			calendar.add(Calendar.SECOND, 1)
			when (period) {
				FinancialStatisticParams.Period.Day -> {
					calendar.add(Calendar.DAY_OF_MONTH, -1)
				}
				
				FinancialStatisticParams.Period.Month -> {
					calendar.add(Calendar.MONTH, -1)
				}
				
				FinancialStatisticParams.Period.Year -> {
					calendar.add(Calendar.YEAR, -1)
				}
			}
			calendar.timeInMillis
		}
		
		val previous: PeriodValue by lazy {
			val calendar = CalendarInstance(end)
			when (period) {
				FinancialStatisticParams.Period.Day -> {
					calendar.add(Calendar.DAY_OF_MONTH, -1)
				}
				
				FinancialStatisticParams.Period.Month -> {
					calendar.add(Calendar.MONTH, -1)
				}
				
				FinancialStatisticParams.Period.Year -> {
					calendar.add(Calendar.YEAR, -1)
				}
			}
			PeriodValue(period, calendar.timeInMillis)
		}
		
	}
	
	fun test() = CoroutineScope(Dispatchers.Default).launch {

//		saveCategory(
//			FinancialCategory(
//				id = 4L,
//				name = "Test Category 4",
//				position = 0,
//				icon = null,
//			)
//		)
//
//		transactionDao.getEarliestTransactionDate(1L, 3L).also {
//			logcat.debug("Earliest date: ${it.format(TimestampStyle.YearMonthDayHourMinuteSeconds)}")
//		}
//
//		transactionDao.getEarliestTransactionDate(1L, 4L).also {
//			logcat.debug("Earliest date: ${it.format(TimestampStyle.YearMonthDayHourMinuteSeconds)}")
//		}

//		initTestData()
		measureTimeMillis {
			refreshStatistics(true)
		}.also { logcat.debug("Statistic built in $it millis") }

//		val date = System.currentTimeMillis()
//		val initialPeriod = PeriodValue(period = FinancialStatisticParams.Period.Day, date)
//		val periodsCount =
//			FinancialStatisticParams.Calculation.AveragePreviousPeriodsForMonth6.getPeriodsCount(
//				initialPeriod.previous
//			)
//		logcat.debug("Periods count: $periodsCount")


//		FinancialStatisticParams.Period.entries.forEach { period ->
//			val periodValue1 = PeriodValue(period, date).also {
//				logcat.debug(
//					it.period,
//					it.start.format(TimestampStyle.YearMonthDayHourMinuteSeconds),
//					it.end.format(TimestampStyle.YearMonthDayHourMinuteSeconds),
//				)
//			}
//
//			val periodValue2 = periodValue1.getPreviousPeriodValue().also {
//				logcat.debug(
//					it.period,
//					it.start.format(TimestampStyle.YearMonthDayHourMinuteSeconds),
//					it.end.format(TimestampStyle.YearMonthDayHourMinuteSeconds),
//				)
//			}
//
//			val periodValue3 = periodValue2.getPreviousPeriodValue().also {
//				logcat.debug(
//					it.period,
//					it.start.format(TimestampStyle.YearMonthDayHourMinuteSeconds),
//					it.end.format(TimestampStyle.YearMonthDayHourMinuteSeconds),
//				)
//			}
//
//		}
	
	
	}
	
	private suspend fun initTestData() {
		logcat.debug("Create initial data")
		val t = measureTimeMillis {
			saveAccount(
				FinancialAccount(
					id = 1L,
					name = "Test Account",
					currency = AccountCurrency.EUR,
					color = FinancialColor.Blue,
					balance = 0.0,
					position = 0,
				)
			)
			
			saveCategory(
				FinancialCategory(
					id = 1L,
					name = "Test Category 1",
					position = 0,
					icon = null,
				)
			)
			
			saveCategory(
				FinancialCategory(
					id = 2L,
					name = "Test Category 2",
					position = 0,
					icon = null,
				)
			)
			
			saveCategory(
				FinancialCategory(
					id = 3L,
					name = "Test Category 3",
					position = 0,
					icon = null,
				)
			)
			
			val timestamp = System.currentTimeMillis()
			val random = Random(System.currentTimeMillis())
			repeat(8000) {
				saveTransaction(
					FinancialTransaction(
						accountId = 1L,
						value = random.nextDouble(-5.0, 40.0),
						comment = null,
						categoryId = random.nextLong(1L, 4L),
						date = random.nextLong(timestamp - monthInMillis * 24, timestamp)
					)
				)
			}
		}
		logcat.debug("Data created in $t millis")
	}
	
	
}