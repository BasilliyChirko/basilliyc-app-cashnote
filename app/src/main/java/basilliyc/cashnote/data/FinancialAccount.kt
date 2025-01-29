package basilliyc.cashnote.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity
data class FinancialAccount(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
	val currency: AccountCurrency,
	val color: AccountColor?,
	val balance: Double,
	val position: Int,
)

enum class AccountCurrency {
	UAH, USD, EUR,
}

enum class AccountColor {
	Orange, Green, Blue, Yellow, Purple, Turquoise,
}

val AccountCurrency.symbol: String
	get() = when (this) {
		AccountCurrency.UAH -> "₴"
		AccountCurrency.USD -> "$"
		AccountCurrency.EUR -> "€"
	}

val AccountColor.color: Color
	@Composable
	get() = when (this) {
		AccountColor.Orange -> if (isDarkTheme()) AccountColorNightOrange else AccountColorDayOrange
		AccountColor.Green -> if (isDarkTheme()) AccountColorNightGreen else AccountColorDayGreen
		AccountColor.Blue -> if (isDarkTheme()) AccountColorNightBlue else AccountColorDayBlue
		AccountColor.Yellow -> if (isDarkTheme()) AccountColorNightYellow else AccountColorDayYellow
		AccountColor.Purple -> if (isDarkTheme()) AccountColorNightPurple else AccountColorDayPurple
		AccountColor.Turquoise -> if (isDarkTheme()) AccountColorNightTurquoise else AccountColorDayTurquoise
	}