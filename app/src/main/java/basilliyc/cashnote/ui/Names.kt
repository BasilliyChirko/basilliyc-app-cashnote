package basilliyc.cashnote.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.R
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
			StatisticSelectedPeriod.AllTime -> R.string.statistic_selected_period_all_time
		}
	)