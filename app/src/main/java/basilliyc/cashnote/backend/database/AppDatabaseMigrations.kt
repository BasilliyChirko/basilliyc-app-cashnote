package basilliyc.cashnote.backend.database

import android.annotation.SuppressLint
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import basilliyc.cashnote.utils.Logcat
import basilliyc.cashnote.utils.getLong
import basilliyc.cashnote.utils.map

object AppDatabaseMigrations {
	
	private val logcat by lazy { Logcat(this) }
	
	private fun createMigration(
		from: Int,
		to: Int,
		call: SupportSQLiteDatabase.() -> Unit,
	): Migration {
		return object : Migration(from, to) {
			override fun migrate(database: SupportSQLiteDatabase) {
				try {
					logcat.info("Database migration from $from to $to was started")
					call(database)
					logcat.info("Database migration from $from to $to was successful")
				} catch (t: Throwable) {
					logcat.error(t, "Database migration from $from to $to was failed")
				}
			}
		}
	}
	
	@SuppressLint("Range")
	fun getAllMigrations(): List<Migration> = listOf(
		createMigration(1, 2) {
			val tableCategoryToAccount = "FinancialCategoryToFinancialAccountParams"
			execSQL("CREATE TABLE IF NOT EXISTS `${tableCategoryToAccount}` (`accountId` INTEGER NOT NULL, `categoryId` INTEGER NOT NULL, `visible` INTEGER NOT NULL, PRIMARY KEY(`accountId`, `categoryId`), FOREIGN KEY(`accountId`) REFERENCES `FinancialAccount`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`categoryId`) REFERENCES `FinancialCategory`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
			execSQL("CREATE INDEX IF NOT EXISTS `index_FinancialCategoryToFinancialAccountParams_accountId` ON `${tableCategoryToAccount}` (`accountId`)")
			execSQL("CREATE INDEX IF NOT EXISTS `index_FinancialCategoryToFinancialAccountParams_categoryId` ON `${tableCategoryToAccount}` (`categoryId`)")
			
			val accountsIds = query("SELECT (id) FROM FinancialAccount").use {
				it.map { it.getLong("id") }
			}
			
			val categoriesIds = query("SELECT (id) FROM FinancialCategory").use {
				it.map { it.getLong("id") }
			}
			
			for (accountId in accountsIds) {
				for (categoryId in categoriesIds) {
					execSQL("INSERT INTO `${tableCategoryToAccount}` (`accountId`, `categoryId`, `visible`) VALUES ($accountId, $categoryId, 1)")
				}
			}
		},
		createMigration(2, 3) {
			val tableName = "FinancialCurrencyRate"
			execSQL("CREATE TABLE IF NOT EXISTS `${tableName}` (`date` INTEGER NOT NULL, `from` TEXT NOT NULL, `to` TEXT NOT NULL, `rate` REAL NOT NULL, PRIMARY KEY(`date`, `from`, `to`))")
			execSQL("CREATE INDEX IF NOT EXISTS `index_FinancialCurrencyRate_from` ON `${tableName}` (`from`)")
			execSQL("CREATE INDEX IF NOT EXISTS `index_FinancialCurrencyRate_to` ON `${tableName}` (`to`)")
		},
	)
	
}