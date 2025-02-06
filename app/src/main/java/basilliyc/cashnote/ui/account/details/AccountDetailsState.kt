package basilliyc.cashnote.ui.account.details

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialStatisticParams

data class AccountDetailsState(
	val page: Page = Page.Loading,
	val dialog: Dialog? = null,
	val result: Result? = null,
	val showBackButton: Boolean,
) {
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val account: FinancialAccount,
			val statisticParams: FinancialStatisticParams,
			val balancePrimaryPositive: Double,
			val balancePrimaryNegative: Double,
			val balanceSecondaryPositive: Double?,
			val balanceSecondaryNegative: Double?,
			val categories: List<CategoryWithBalance>,
		) : Page
	}
	
	data class CategoryWithBalance(
		val category: FinancialCategory,
		val primaryValue: Double,
		val secondaryValue: Double?,
	)
	
	sealed interface Dialog {
		data object AccountDeleteConfirmation : Dialog
	}
	
	sealed interface Result {
		data object NavigateCategoryList : Result
		data class NavigateTransactionForm(val accountId: Long, val categoryId: Long) : Result
		data class NavigateAccountForm(val accountId: Long) : Result
		data class NavigateAccountHistory(val accountId: Long) : Result
		data object AccountDeletionSuccess : Result
		data object AccountDeletionError : Result
	}
	
}