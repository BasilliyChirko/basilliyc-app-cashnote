package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
	foreignKeys = [
		ForeignKey(
			entity = FinancialAccount::class,
			parentColumns = ["id"],
			childColumns = ["accountId"],
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = FinancialCategory::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("accountId"),
		Index("categoryId"),
	],
	primaryKeys = ["accountId", "categoryId"]
)
data class FinancialCategoryToFinancialAccountParams(
	val accountId: Long,
	val categoryId: Long,
	val visible: Boolean = true,
)

