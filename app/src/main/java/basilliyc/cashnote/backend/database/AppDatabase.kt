package basilliyc.cashnote.backend.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import basilliyc.cashnote.data.Account

@Database(
	entities = [
		Account::class
	],
	exportSchema = true,
	version = 1, //previous version: 1
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun accountRepository(): AppDatabaseAccountRepository
}