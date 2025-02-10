package basilliyc.cashnote.ui.settings

import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.settings.SettingsStateHolder.Common
import basilliyc.cashnote.ui.theme.ThemeMode
import basilliyc.cashnote.utils.flowZip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel(), SettingsListener {
	
	val state = SettingsStateHolder(
		page = SettingsStateHolder.Page.Data(
			common = Common(
				themeMode = preferences.themeMode.value,
			)
		)
	)
	
	init {
		viewModelScope.launch {
			flowZip(
				preferences.themeMode.flow,
			) { themeMode ->
				Common(
					themeMode = themeMode,
				)
			}.collectLatest {
				state.pageDataCommon = it
			}
		}
	}
	
	override fun onThemeModeChanged(themeMode: ThemeMode) {
		schedule {
			preferences.themeMode.value = themeMode
		}
	}
	
}