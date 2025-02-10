package basilliyc.cashnote.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.ui.theme.ThemeMode

class SettingsStateHolder(
	page: Page,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page as? Page.Data
		set(value) {
			if (value == null) return
			page = value
		}
	
	var pageDataCommon
		get() = pageData?.common
		set(value) {
			if (value == null) return
			pageData = pageData?.copy(common = value)
		}
	
	sealed interface Page {
		data class Data(
			val common: Common,
		) : Page
	}
	
	data class Common(
		val themeMode: ThemeMode,
	)
	
}


