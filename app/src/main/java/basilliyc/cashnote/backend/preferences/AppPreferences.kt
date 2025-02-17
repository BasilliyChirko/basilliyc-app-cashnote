package basilliyc.cashnote.backend.preferences

import basilliyc.cashnote.backend.preferences.base.BasePreferences
import basilliyc.cashnote.ui.theme.ThemeMode

class AppPreferences : BasePreferences() {
	
	val accountIdOnNavigation = longOrNull("accountIdOnNavigation")
	
	val themeMode = enum("themeMode", ThemeMode.System)
	
	val accountListSingleLine = boolean("accountListSingleLine", true)
	
	val accountListQuickTransaction = boolean("accountListQuickTransaction", false)
	
}