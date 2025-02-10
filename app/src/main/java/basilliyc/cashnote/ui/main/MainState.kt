package basilliyc.cashnote.ui.main

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.ui.theme.ThemeMode

data class MainState(
	val accountOnNavigation: FinancialAccount?,
	val isNeedRestartActivity: Boolean = false,
	val themeMode: ThemeMode,
)
