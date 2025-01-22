package basilliyc.cashnote

import android.app.Application
import basilliyc.cashnote.backend.manager.AccountManager
import basilliyc.cashnote.utils.Logcat
import basilliyc.cashnote.utils.takeIf
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
	
	private val accountManager: AccountManager by inject()
	
	override fun onCreate() {
		super.onCreate()
		
		val logEnabled = BuildConfig.DEBUG
		Logcat.isConsoleLogEnabled = logEnabled
		Logcat.crashlyticsInstance = takeIf(logEnabled) { FirebaseCrashlytics.getInstance() }
		
		startKoin {
			androidContext(applicationContext)
			modules(AppValues.koinModules())
		}
		
		accountManager.test()
		
	}
	
}