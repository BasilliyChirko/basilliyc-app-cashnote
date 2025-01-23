package basilliyc.cashnote.ui.account.form

import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.ui.components.TextFieldError

object AccountFormState {
	
	data class Page(
		val content: Content = Content.Loading,
	)
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val isNew: Boolean,
			val currency: AccountCurrency,
			val name: String,
			val nameError: TextFieldError? = null,
			val balance: String,
			val balanceError: TextFieldError? = null,
			val color: AccountColor?,
		) : Content {
			constructor(account: Account) : this(
				isNew = account.id == 0L,
				currency = account.currency,
				name = account.name,
				nameError = null,
				balance = account.balance.toString(),
				balanceError = null,
				color = account.color,
			)
		}
	}
	
}