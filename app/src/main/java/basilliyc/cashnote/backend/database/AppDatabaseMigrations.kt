package basilliyc.cashnote.backend.database

import android.annotation.SuppressLint
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import basilliyc.cashnote.utils.Logcat

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
		//Here will be declared all migrations
	)
	
}