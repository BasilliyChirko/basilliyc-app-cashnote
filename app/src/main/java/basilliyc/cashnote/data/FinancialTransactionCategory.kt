package basilliyc.cashnote.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FinancialTransactionCategory(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
	val icon: FinancialTransactionCategoryIcon?,
)


enum class FinancialTransactionCategoryIcon(val imageVector: ImageVector) {
	Home(Icons.Filled.Home),
	Family(Icons.Filled.FamilyRestroom),
	Person(Icons.Filled.Person),
	Person2(Icons.Filled.Person2),
	Person3(Icons.Filled.Person3),
	Person4(Icons.Filled.Person4),
	Car(Icons.Filled.DriveEta),
	Food(Icons.Filled.Fastfood),
	Alcohol(Icons.Filled.WineBar),
	Gift(Icons.Filled.CardGiftcard),
}