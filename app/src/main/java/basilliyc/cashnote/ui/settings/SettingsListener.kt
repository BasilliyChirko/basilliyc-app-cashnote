package basilliyc.cashnote.ui.settings

import android.app.Activity
import basilliyc.cashnote.ui.base.BaseListener
import basilliyc.cashnote.ui.theme.ThemeMode

interface SettingsListener : BaseListener {
	fun onThemeModeChanged(themeMode: ThemeMode)
	fun onBackupCreateClicked(activity: Activity)
	fun onBackupRestoreClicked()
	fun onBackupRestoreConfirmed()
	fun onBackupRestoreCanceled()
}