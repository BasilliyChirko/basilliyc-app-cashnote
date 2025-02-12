package basilliyc.cashnote.ui.account.form

import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.ui.base.BaseListener

interface AccountFormListener: BaseListener {
	fun onNameChanged(name: String) {}
	fun onBalanceChanged(balance: String) {}
	fun onCurrencyChanged(currency: FinancialCurrency) {}
	fun onColorChanged(color: FinancialColor?) {}
	fun onSaveClicked() {}
	fun onShowOnNavigationChanged(isShowOnNavigation: Boolean) {}
	fun onCategoryClicked(categoryId: Long)
}