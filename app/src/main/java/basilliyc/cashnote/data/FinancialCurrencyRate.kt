package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.Index

@Entity(
	primaryKeys = ["date", "from", "to"],
	indices = [
		Index("from"),
		Index("to"),
	]
)
data class FinancialCurrencyRate(
	val date: Long,
	val from: FinancialCurrency,
	val to: FinancialCurrency,
	val rate: Double,
)