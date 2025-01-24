package basilliyc.cashnote.backend.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.Transaction

@Database(
	entities = [
		Account::class,
		Transaction::class,
	],
	exportSchema = true,
	version = 2, //previous version: 1
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun accountRepository(): AppDatabaseAccountRepository
	abstract fun transactionRepository(): AppDatabaseTransactionRepository
}