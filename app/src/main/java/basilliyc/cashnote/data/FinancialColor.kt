package basilliyc.cashnote.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.ui.theme.AccountColorDayBlue
import basilliyc.cashnote.ui.theme.AccountColorDayGreen
import basilliyc.cashnote.ui.theme.AccountColorDayOrange
import basilliyc.cashnote.ui.theme.AccountColorDayPurple
import basilliyc.cashnote.ui.theme.AccountColorDayTurquoise
import basilliyc.cashnote.ui.theme.AccountColorDayYellow
import basilliyc.cashnote.ui.theme.AccountColorNightBlue
import basilliyc.cashnote.ui.theme.AccountColorNightGreen
import basilliyc.cashnote.ui.theme.AccountColorNightOrange
import basilliyc.cashnote.ui.theme.AccountColorNightPurple
import basilliyc.cashnote.ui.theme.AccountColorNightTurquoise
import basilliyc.cashnote.ui.theme.AccountColorNightYellow
import basilliyc.cashnote.ui.theme.isDarkTheme
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.theme.AccountColorDayRed
import basilliyc.cashnote.ui.theme.AccountColorNightRed

enum class FinancialColor {
	Red, Orange, Green, Blue, Yellow, Purple, Turquoise,
}

val FinancialColor?.color: Color
	@Composable
	get() = when (this) {
		FinancialColor.Red -> if (isDarkTheme()) AccountColorNightRed else AccountColorDayRed
		FinancialColor.Orange -> if (isDarkTheme()) AccountColorNightOrange else AccountColorDayOrange
		FinancialColor.Green -> if (isDarkTheme()) AccountColorNightGreen else AccountColorDayGreen
		FinancialColor.Blue -> if (isDarkTheme()) AccountColorNightBlue else AccountColorDayBlue
		FinancialColor.Yellow -> if (isDarkTheme()) AccountColorNightYellow else AccountColorDayYellow
		FinancialColor.Purple -> if (isDarkTheme()) AccountColorNightPurple else AccountColorDayPurple
		FinancialColor.Turquoise -> if (isDarkTheme()) AccountColorNightTurquoise else AccountColorDayTurquoise
		null -> Color.Unspecified
	}

val FinancialColor?.text: String
	@Composable
	get() = stringResource(
		when (this) {
			FinancialColor.Red -> R.string.account_color_red
			FinancialColor.Orange -> R.string.account_color_orange
			FinancialColor.Green -> R.string.account_color_green
			FinancialColor.Blue -> R.string.account_color_blue
			FinancialColor.Yellow -> R.string.account_color_yellow
			FinancialColor.Purple -> R.string.account_color_purple
			FinancialColor.Turquoise -> R.string.account_color_turquoise
			null -> R.string.account_color_none
		}
	)