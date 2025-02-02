package basilliyc.cashnote.data

data class FinancialDeviation(
	val accountId: Long,
	val categoryId: Long,
	val primaryValue: Double,
	val secondaryValue: Double,
)

data class FinancialDeviationParams(
	val showAccountDeviation: Boolean,
	val period: Period,
	val primaryValueCalculation: Calculation,
	val secondaryValueCalculation: Calculation,
	val showSecondaryForCategory: Boolean,
	val showSecondaryForAccount: Boolean,
) {
	enum class Period {
		Day, Week, Moth, Year
	}
	
	enum class Calculation {
		CurrentPeriod,
		PreviousPeriod,
		AveragePreviousPeriodsForWeek,
		AveragePreviousPeriodsForMonth,
		AveragePreviousPeriodsForYear,
		AveragePreviousPeriodsForAllTime,
	}
	
}
