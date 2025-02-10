package basilliyc.cashnote.ui.account.form

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.ui.components.TextFieldState

data class AccountFormState(
	val page: Page = Page.Loading,
	val action: Action? = null,
) {
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val isNew: Boolean,
			val currency: FinancialCurrency,
			val name: TextFieldState,
			val balance: TextFieldState,
			val color: FinancialColor?,
			val isShowOnNavigation: Boolean,
		) : Page {
			constructor(account: FinancialAccount, isShowOnNavigation: Boolean) : this(
				isNew = account.id == 0L,
				currency = account.currency,
				name = TextFieldState(value = account.name),
				balance = TextFieldState(value = account.balance.toString()),
				color = account.color,
				isShowOnNavigation = isShowOnNavigation,
			)
		}
	}
	
	sealed interface Action {
		data class SaveSuccess(val isNew: Boolean, val isNeedRebuildApp: Boolean) : Action
		data object Cancel : Action
		data object SaveError : Action
	}
	
}