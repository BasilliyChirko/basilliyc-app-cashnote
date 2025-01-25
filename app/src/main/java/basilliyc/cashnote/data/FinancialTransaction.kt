package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	foreignKeys = [
		ForeignKey(
			entity = FinancialAccount::class,
			parentColumns = ["id"],
			childColumns = ["accountId"],
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = FinancialTransactionCategory::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.SET_NULL,
		),
	],
	indices = [
		Index("accountId"),
		Index("categoryId"),
	]
)
data class FinancialTransaction(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val value: Double,
	val date: Long,
	val comment: String,
	val accountId: Long,
	val categoryId: Long?,
)