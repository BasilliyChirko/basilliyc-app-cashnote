package basilliyc.cashnote.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.R
import basilliyc.cashnote.data.StatisticMonth
import basilliyc.cashnote.data.StatisticSelectedPeriod
import basilliyc.cashnote.ui.theme.ThemeMode


val ThemeMode.stringName
	@Composable
	get() = stringResource(
		when (this) {
			ThemeMode.Night -> R.string.theme_mode_night
			ThemeMode.Day -> R.string.theme_mode_day
			ThemeMode.System -> R.string.theme_mode_system
		}
	)


val StatisticSelectedPeriod.stringName
	@Composable
	get() = stringResource(
		when (this) {
			StatisticSelectedPeriod.OneMonth -> R.string.statistic_selected_period_one_month
			StatisticSelectedPeriod.ThreeMonths -> R.string.statistic_selected_period_three_months
			StatisticSelectedPeriod.SixMonths -> R.string.statistic_selected_period_six_months
			StatisticSelectedPeriod.OneYear -> R.string.statistic_selected_period_one_year
//			StatisticSelectedPeriod.AllTime -> R.string.statistic_selected_period_all_time
		}
	)

val StatisticMonth.stringName
	get() = @Composable {
		val monthName = stringResource(
			when (this.month) {
				0 -> R.string.statistic_month_january
				1 -> R.string.statistic_month_february
				2 -> R.string.statistic_month_march
				3 -> R.string.statistic_month_april
				4 -> R.string.statistic_month_may
				5 -> R.string.statistic_month_june
				6 -> R.string.statistic_month_july
				7 -> R.string.statistic_month_august
				8 -> R.string.statistic_month_september
				9 -> R.string.statistic_month_october
				10 -> R.string.statistic_month_november
				11 -> R.string.statistic_month_december
				else -> throw IllegalStateException("Unknown month: $this")
			}
		)
		
		"$monthName ${this.year}"
	}