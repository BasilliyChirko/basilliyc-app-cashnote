package basilliyc.cashnote.ui.account.list

import basilliyc.cashnote.data.FinancialAccount

data class AccountListState(
	val content: Content = Content.Loading,
) {
	
	sealed interface Content {
		data object Loading : Content
		data object DataEmpty : Content
		data class Data(
			val accounts: List<AccountBalance>,
		) : Content
	}
	
	data class AccountBalance(
		val account: FinancialAccount,
		val primaryValue: Double?,
	)
	
}