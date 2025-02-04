package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	foreignKeys = [
		ForeignKey(
			entity = FinancialAccount::class,
			parentColumns = ["id"],
			childColumns = ["accountId"],
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = FinancialCategory::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("accountId"),
		Index("categoryId"),
	],
	primaryKeys = ["accountId", "categoryId"]
)
data class FinancialStatistic(
	val accountId: Long,
	val categoryId: Long,
	val primaryValuePositive: Double,
	val primaryValueNegative: Double,
	val secondaryValuePositive: Double,
	val secondaryValueNegative: Double,
)

@Entity
data class FinancialStatisticParams(
	@PrimaryKey val id: Long = 0,
	val calculationValidUntil: Long = 0,
	val period: Period = Period.Month,
	val primaryValueCalculation: Calculation = Calculation.CurrentPeriod,
	val secondaryValueCalculation: Calculation = Calculation.PreviousPeriod,
	val showAccountStatistic: Boolean = true,
	val showSecondaryValueForCategory: Boolean = true,
	val showSecondaryValueForAccount: Boolean = true,
) {
	enum class Period {
		Day, Month, Year
	}
	
	enum class Calculation {
		CurrentPeriod,
		PreviousPeriod,
		AveragePreviousPeriodsForMonth1,
		AveragePreviousPeriodsForMonth3,
		AveragePreviousPeriodsForMonth6,
		AveragePreviousPeriodsForYear,
		AveragePreviousPeriodsForAllTime,
	}
}
