package basilliyc.cashnote.ui.account.form

import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialColor

interface AccountFormListener {
	fun onActionConsumed() {}
	fun onNameChanged(name: String) {}
	fun onBalanceChanged(balance: String) {}
	fun onCurrencyChanged(currency: AccountCurrency) {}
	fun onColorChanged(color: FinancialColor?) {}
	fun onSaveClicked() {}
	fun onShowOnNavigationChanged(isShowOnNavigation: Boolean) {}
}