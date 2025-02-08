package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FinancialCategory(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
	val icon: FinancialIcon?,
	val color: FinancialColor?,
	val position: Int = id.toInt(),
)