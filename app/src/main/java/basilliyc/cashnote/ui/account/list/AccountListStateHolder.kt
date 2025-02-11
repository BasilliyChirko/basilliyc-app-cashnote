package basilliyc.cashnote.ui.account.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.utils.castOrNull

class AccountListStateHolder(
	page: Page = Page.Loading,
	result: Result? = null,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page.castOrNull<Page.Data>()
		set(value) = if (value != null) page = value else Unit
	
	var result by mutableStateOf(result)
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val accounts: List<AccountBalance>,
			val accountsDragged: List<AccountBalance>?,
		) : Page
	}
	
	data class AccountBalance(
		val account: FinancialAccount,
		val primaryValue: Double?,
	)
	
	sealed interface Result {
		data class NavigateAccountDetails(val id: Long) : Result
		data object NavigateAccountForm : Result
	}
	
}