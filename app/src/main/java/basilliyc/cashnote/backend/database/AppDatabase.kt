package basilliyc.cashnote.backend.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCategoryToFinancialAccountParams
import basilliyc.cashnote.data.FinancialStatistic
import basilliyc.cashnote.data.FinancialStatisticParams

@Database(
	entities = [
		FinancialAccount::class,
		FinancialTransaction::class,
		FinancialCategory::class,
		FinancialStatistic::class,
		FinancialStatisticParams::class,
		FinancialCategoryToFinancialAccountParams::class,
	],
	exportSchema = true,
	version = 2, //previous version: 2
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun accountDao(): FinancialAccountDao
	abstract fun transactionDao(): FinancialTransactionDao
	abstract fun categoryDao(): FinancialCategoryDao
	abstract fun statisticDao(): FinancialStatisticDao
	abstract fun categoryToAccountParamsDao(): FinancialCategoryToFinancialAccountParamsDao
}