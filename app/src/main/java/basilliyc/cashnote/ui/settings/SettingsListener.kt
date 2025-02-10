package basilliyc.cashnote.ui.settings

import basilliyc.cashnote.ui.theme.ThemeMode

interface SettingsListener {
	fun onThemeModeChanged(themeMode: ThemeMode)
}