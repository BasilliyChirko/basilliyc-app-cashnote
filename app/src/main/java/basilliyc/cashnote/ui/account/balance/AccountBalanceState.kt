package basilliyc.cashnote.ui.account.balance

import basilliyc.cashnote.data.Account
import basilliyc.cashnote.ui.components.TextFieldState

object AccountBalanceState {
	
	data class Page(
		val content: Content = Content.Loading,
		val action: Action? = null,
	)
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val account: Account,
			val isBalanceReduce: Boolean? = null,
			val balanceDifference: TextFieldState,
			val balanceNew: TextFieldState,
			val comment: TextFieldState,
		) : Content
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object Cancel : Action
		data object SaveError : Action
	}
}