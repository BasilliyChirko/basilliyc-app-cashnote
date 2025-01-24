package basilliyc.cashnote.ui.account.balance

import basilliyc.cashnote.data.Account
import basilliyc.cashnote.ui.components.TextInputState

object AccountBalanceState {
	
	data class Page(
		val content: Content = Content.Loading,
		val event: Event? = null,
	)
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val account: Account,
			val isBalanceReduce: Boolean? = null,
			val balanceDifference: TextInputState,
			val balanceNew: TextInputState,
			val comment: TextInputState,
		) : Content
	}
	
	sealed interface Event {
		data object Save : Event
		data object Cancel : Event
		data object SaveError : Event
	}
}