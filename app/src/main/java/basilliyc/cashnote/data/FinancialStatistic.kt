package basilliyc.cashnote.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import basilliyc.cashnote.data.FinancialStatisticParams.Calculation.*
import basilliyc.cashnote.R

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


@Composable
fun FinancialStatisticParams.Calculation.labelText(): String = stringResource(
	when (this) {
		CurrentPeriod -> R.string.financial_statistic_params_calculation_current_period_label
		PreviousPeriod -> R.string.financial_statistic_params_calculation_previous_period_label
		AveragePreviousPeriodsForMonth1 -> R.string.financial_statistic_params_calculation_average_previous_periods_for_month1_label
		AveragePreviousPeriodsForMonth3 -> R.string.financial_statistic_params_calculation_average_previous_periods_for_month3_label
		AveragePreviousPeriodsForMonth6 -> R.string.financial_statistic_params_calculation_average_previous_periods_for_month6_label
		AveragePreviousPeriodsForYear -> R.string.financial_statistic_params_calculation_average_previous_periods_for_year_label
		AveragePreviousPeriodsForAllTime -> R.string.financial_statistic_params_calculation_average_previous_periods_for_all_time_label
	}
)