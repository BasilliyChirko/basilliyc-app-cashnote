package basilliyc.cashnote.ui.account_list

import basilliyc.cashnote.data.Account

object AccountListState {
	
	data class Page(
		val content: Content = Content.Loading,
	)
	
	sealed interface Content {
		data object Loading : Content
		data object DataEmpty : Content
		data class Data(
			val accounts: List<Account>,
		) : Content
	}
	
}