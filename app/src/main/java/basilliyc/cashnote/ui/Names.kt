package basilliyc.cashnote.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialCurrency
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