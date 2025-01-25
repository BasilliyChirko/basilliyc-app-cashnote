package basilliyc.cashnote

import androidx.room.Room
import basilliyc.cashnote.backend.database.AppDatabase
import basilliyc.cashnote.backend.database.AppDatabaseMigrations
import basilliyc.cashnote.backend.manager.FinancialManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun AppValues.koinModules() = module {
	
	single { FinancialManager() }
	
	single {
		Room.databaseBuilder(androidContext(), AppDatabase::class.java, "AppDatabase")
			.addMigrations(*AppDatabaseMigrations.getAllMigrations().toTypedArray())
			.apply {
				if (BuildConfig.DEBUG) {
					fallbackToDestructiveMigration()
				}
			}
			.allowMainThreadQueries()
			.build()
	}
	
	single { get<AppDatabase>().accountRepository() }
	single { get<AppDatabase>().transactionRepository() }
	single { get<AppDatabase>().transactionCategoryRepository() }
	
}