package basilliyc.cashnote.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import basilliyc.cashnote.ui.theme.AccountColorBlue
import basilliyc.cashnote.ui.theme.AccountColorGreen
import basilliyc.cashnote.ui.theme.AccountColorOrange
import basilliyc.cashnote.ui.theme.AccountColorPurple
import basilliyc.cashnote.ui.theme.AccountColorTurquoise
import basilliyc.cashnote.ui.theme.AccountColorYellow

@Entity
data class FinancialAccount(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
	val currency: AccountCurrency,
	val color: AccountColor?,
	val balance: Double,
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
	get() = when (this) {
		AccountColor.Orange -> AccountColorOrange
		AccountColor.Green -> AccountColorGreen
		AccountColor.Blue -> AccountColorBlue
		AccountColor.Yellow -> AccountColorYellow
		AccountColor.Purple -> AccountColorPurple
		AccountColor.Turquoise -> AccountColorTurquoise
	}