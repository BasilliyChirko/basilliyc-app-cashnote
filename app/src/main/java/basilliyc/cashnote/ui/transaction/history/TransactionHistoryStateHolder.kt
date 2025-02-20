package basilliyc.cashnote.ui.transaction.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialTransaction

class TransactionHistoryStateHolder(
	page: Page = Page(),
	result: Result? = null,
) {
	
	var page by mutableStateOf(page)
	
	var result by mutableStateOf(result)
	
	
	data class Page(
		val singleAccount: FinancialAccount? = null,
		val initialLoading: Boolean = true,
		val initialLoadingError: Throwable? = null,
		val accounts: Map<Long, FinancialAccount> = emptyMap(),
		val categories: Map<Long, FinancialCategory> = emptyMap(),
		val transactions: List<FinancialTransaction> = emptyList(),
		val transactionsLoadingMore: Boolean = false,
		val transactionsLoadingMoreError: Throwable? = null,
		val showBackButton: Boolean = false,
	)
	
	sealed interface Result {
		data class EditTransaction(
			val accountId: Long,
			val categoryId: Long,
			val transactionId: Long,
		) : Result
	}
	
}