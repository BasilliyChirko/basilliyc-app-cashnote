package basilliyc.cashnote

import android.content.Context
import androidx.room.Room
import basilliyc.cashnote.backend.database.AppDatabase
import basilliyc.cashnote.backend.database.AppDatabaseMigrations
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.manager.currency_rate.FinancialCurrencyRateManager
import basilliyc.cashnote.backend.manager.currency_rate.FinancialCurrencyRateRepositoryFixer
import basilliyc.cashnote.backend.manager.currency_rate.FinancialCurrencyRateRepositoryFixerTest
import basilliyc.cashnote.backend.manager.currency_rate.FinancialCurrencyRateRepositoryMonobank
import basilliyc.cashnote.backend.preferences.AppPreferences
import basilliyc.cashnote.backend.preferences.FinancialCurrencyRatePreferences
import basilliyc.cashnote.backend.preferences.StatisticPreferences
import basilliyc.cashnote.utils.FullPrintHttpLogging
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun AppValues.koinModules() = module {
	
	//MANAGERS
	
	single { FinancialManager() }
	single { FinancialCurrencyRateManager() }
	single { AppPreferences() }
	single { StatisticPreferences() }
	single { FinancialCurrencyRatePreferences() }
	
	factory { get<Context>().contentResolver }
	
	
	//LOCAL DATABASE
	
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
	single { get<AppDatabase>().currencyRateDao() }
	
	//INTERNET RETROFIT
	
	factory {
		val client = OkHttpClient.Builder()
			.addInterceptor(HttpLoggingInterceptor(FullPrintHttpLogging()))
			.build()
		Retrofit.Builder()
			.addCallAdapterFactory(CoroutineCallAdapterFactory())
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
	}
	
	single {
		get<Retrofit.Builder>()
			.baseUrl(MONOBANK_BASE_URL)
			.build()
			.create(FinancialCurrencyRateRepositoryMonobank::class.java)
	}
//	single {
//		get<Retrofit.Builder>()
//			.baseUrl(FIXER_BASE_URL)
//			.build()
//			.create(FinancialCurrencyRateRepositoryFixer::class.java)
//	}
	single {
		FinancialCurrencyRateRepositoryFixerTest() as FinancialCurrencyRateRepositoryFixer
	}
}