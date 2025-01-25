package basilliyc.cashnote.ui.account.form

import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.ui.components.TextFieldState

object AccountFormState {
	
	data class Page(
		val content: Content = Content.Loading,
		val action: Action? = null,
	)
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val isNew: Boolean,
			val currency: AccountCurrency,
			val name: TextFieldState,
			val balance: TextFieldState,
			val color: AccountColor?,
		) : Content {
			constructor(account: Account) : this(
				isNew = account.id == 0L,
				currency = account.currency,
				name = TextFieldState(value = account.name),
				balance = TextFieldState(value = account.balance.toString()),
				color = account.color,
			)
		}
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object Cancel : Action
		data object SaveError : Action
	}
	
}