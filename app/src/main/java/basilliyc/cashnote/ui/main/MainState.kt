package basilliyc.cashnote.ui.main

import basilliyc.cashnote.data.FinancialAccount

data class MainState(
	val accountOnNavigation: FinancialAccount?,
	val isNeedRestartActivity: Boolean = false
)
