package basilliyc.cashnote.ui.account.details

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory

data class AccountDetailsState(
	val page: Page = Page.Loading
) {
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val account: FinancialAccount,
			val showBalanceProfit: Boolean,
			val balanceSpend: Double?,
			val balanceReceive: Double?,
			val categories: List<CategoryWithBalance>,
		): Page
	}
	
	data class CategoryWithBalance(
		val category: FinancialCategory,
		val balance: Double,
		val deviation: Double?,
	)
	
}