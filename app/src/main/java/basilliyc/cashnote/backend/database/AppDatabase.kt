package basilliyc.cashnote.backend.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.data.FinancialCategory

@Database(
	entities = [
		FinancialAccount::class,
		FinancialTransaction::class,
		FinancialCategory::class,
	],
	exportSchema = true,
	version = 3, //previous version: 1
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun accountRepository(): DatabaseAccountRepository
	abstract fun transactionRepository(): DatabaseTransactionRepository
	abstract fun transactionCategoryRepository(): DatabaseTransactionCategoryRepository
}