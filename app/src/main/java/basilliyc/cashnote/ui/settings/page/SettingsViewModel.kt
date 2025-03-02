package basilliyc.cashnote.ui.settings.page

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.AppValues
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.theme.ThemeMode
import basilliyc.cashnote.utils.anyTry
import basilliyc.cashnote.utils.flowZip
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel : BaseViewModel(), SettingsListener {
	
	private val sendingFileRequest = registerActivityRequest(
		ActivityResultContracts.StartActivityForResult()
	)
	
	private val pickBackupFileRequest = registerActivityRequest(
		ActivityResultContracts.OpenDocument(),
		arrayOf("application/json")
	)
	
	val state = SettingsStateHolder(
		page = SettingsStateHolder.Page.Data(
			common = SettingsStateHolder.Common(
				themeMode = preferences.themeMode.value,
			)
		)
	)
	
	init {
		viewModelScope.launch {
			flowZip(
				preferences.themeMode.flow,
			) { themeMode ->
				SettingsStateHolder.Common(
					themeMode = themeMode,
				)
			}.collectLatest {
				state.pageDataCommon = it
			}
		}
	}
	
	override fun onResultHandled() {
		state.result = null
	}
	
	override fun onThemeModeChanged(themeMode: ThemeMode) {
		schedule {
			preferences.themeMode.value = themeMode
		}
	}
	
	override fun onBackupCreateClicked(activity: Activity) {
		val timestamp = System.currentTimeMillis()
		val version = AppValues.BACKUP_VERSION
		val fileName = "CashNoteBackup_${version}_$timestamp.json"
		
		val file = File(activity.cacheDir, fileName)
		
		schedule {
			
			file.writeText(financialManager.createBackupString())
			
			
			val fileUri: Uri = FileProvider.getUriForFile(
				activity,
				"${activity.packageName}.fileprovider",
				file
			)
			
			val intent = Intent(Intent.ACTION_SEND).apply {
				type = "application/json"
				putExtra(Intent.EXTRA_STREAM, fileUri)
				addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			}
			
			sendingFileRequest.launch(
				Intent.createChooser(intent, "Share backup using")
			)
			
			anyTry { file.delete() }
		}
	}
	
	override fun onBackupRestoreClicked() {
		state.dialog = SettingsStateHolder.Dialog.RestoreBackupConfirmation
	}
	
	override fun onBackupRestoreConfirmed() {
		schedule {
			try {
				val uri = pickBackupFileRequest.launch() ?: return@schedule
				logcat.debug(uri)
				
				val contentResolver = inject<ContentResolver>().value
				
				val bytes = contentResolver.openInputStream(uri)?.use {
					it.readBytes()
				} ?: return@schedule
				
				val string = String(bytes)
				
				financialManager.restoreBackupString(string)
				state.result = SettingsStateHolder.Result.BackupRestoreSuccess
			} catch (t: Throwable) {
				state.result = SettingsStateHolder.Result.BackupRestoreFailure
			} finally {
				state.dialog = null
			}
		}
	}
	
	override fun onBackupRestoreCanceled() {
		state.dialog = null
	}
	
}