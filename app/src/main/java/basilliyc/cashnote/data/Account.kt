package basilliyc.cashnote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
)