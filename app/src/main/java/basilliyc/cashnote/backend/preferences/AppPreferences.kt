package basilliyc.cashnote.backend.preferences

import basilliyc.cashnote.backend.preferences.base.BasePreferences

class AppPreferences : BasePreferences() {
	
	val accountIdOnNavigation = longOrNull("accountIdOnNavigation")
	
}