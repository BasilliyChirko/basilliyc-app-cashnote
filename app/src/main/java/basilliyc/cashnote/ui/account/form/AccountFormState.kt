package basilliyc.cashnote.ui.account.form

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.ui.components.TextFieldState

data class AccountFormState(
	val content: Content = Content.Loading,
	val action: Action? = null,
) {
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val isNew: Boolean,
			val currency: AccountCurrency,
			val name: TextFieldState,
			val balance: TextFieldState,
			val color: AccountColor?,
		) : Content {
			constructor(financialAccount: FinancialAccount) : this(
				isNew = financialAccount.id == 0L,
				currency = financialAccount.currency,
				name = TextFieldState(value = financialAccount.name),
				balance = TextFieldState(value = financialAccount.balance.toString()),
				color = financialAccount.color,
			)
		}
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object Cancel : Action
		data object SaveError : Action
	}
	
}