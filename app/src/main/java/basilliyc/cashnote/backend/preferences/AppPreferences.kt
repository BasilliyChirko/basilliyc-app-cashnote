package basilliyc.cashnote.backend.preferences

import android.content.Context
import android.content.SharedPreferences
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppPreferences {
	
	val prefs: SharedPreferences by lazy {
		inject<Context>().value.getSharedPreferences(
			this::class.java.simpleName,
			Context.MODE_PRIVATE
		)
	}
	
	
	var accountIdOnNavigation: Long?
		get() = prefs.getLong("accountIdOnNavigation", -1L).takeIf { it > 0 }
		set(value) {
			prefs.edit().putLong("accountIdOnNavigation", value ?: -1L).apply()
			CoroutineScope(Dispatchers.Default).launch {
				accountIdOnNavigationAsMutableFlow.emit(value)
			}
		}
	
//	private val accountIdOnNavigationAsMutableFlow by lazy { MutableStateFlow(accountIdOnNavigation) }
//	val accountIdOnNavigationAsFlow by lazy { accountIdOnNavigationAsMutableFlow.asStateFlow() }
	
	private val accountIdOnNavigationAsMutableFlow = MutableStateFlow(accountIdOnNavigation)
	fun accountIdOnNavigationAsFlow() = accountIdOnNavigationAsMutableFlow.asStateFlow()
	
	
}