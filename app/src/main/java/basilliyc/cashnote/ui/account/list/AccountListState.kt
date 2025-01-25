package basilliyc.cashnote.ui.account.list

import basilliyc.cashnote.data.FinancialAccount

object AccountListState {
	
	data class Page(
		val content: Content = Content.Loading,
	)
	
	sealed interface Content {
		data object Loading : Content
		data object DataEmpty : Content
		data class Data(
			val financialAccounts: List<FinancialAccount>,
		) : Content
	}
	
}