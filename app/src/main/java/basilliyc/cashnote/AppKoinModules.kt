package basilliyc.cashnote

import android.content.Context
import androidx.room.Room
import basilliyc.cashnote.backend.database.AppDatabase
import basilliyc.cashnote.backend.database.AppDatabaseMigrations
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun AppValues.koinModules() = module {
	
	single { FinancialManager() }
	single { AppPreferences() }
	
	factory { get<Context>().contentResolver }
	
	single {
		Room.databaseBuilder(androidContext(), AppDatabase::class.java, "AppDatabase")
			.addMigrations(*AppDatabaseMigrations.getAllMigrations().toTypedArray())
			.apply {
//				if (BuildConfig.DEBUG) {
//					fallbackToDestructiveMigration()
//				}
			}
//			.allowMainThreadQueries()
			.build()
	}
	
	single { get<AppDatabase>().accountDao() }
	single { get<AppDatabase>().transactionDao() }
	single { get<AppDatabase>().categoryDao() }
	single { get<AppDatabase>().statisticDao() }
	single { get<AppDatabase>().categoryToAccountParamsDao() }
	
}