package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FinancialAccount(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
	val currency: FinancialCurrency,
	val color: FinancialColor?,
	val balance: Double,
	val position: Int,
)

enum class FinancialCurrency(val symbol: String, val code: Int) {
	UAH("₴", 980),
	USD("$", 840),
	EUR("€", 978),
}