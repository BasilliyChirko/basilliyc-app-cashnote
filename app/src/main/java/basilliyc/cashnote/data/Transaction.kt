package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
	foreignKeys = [
		ForeignKey(
			entity = Account::class,
			parentColumns = ["id"],
			childColumns = ["accountId"],
			onDelete = ForeignKey.CASCADE,
		)
	]
)
data class Transaction(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val value: Double,
	val date: Long,
	val comment: String,
	val accountId: Long,
)