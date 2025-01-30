package basilliyc.cashnote.ui.account.history

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransaction

data class AccountHistoryState(
	val account: FinancialAccount? = null,
	val initialLoading: Boolean = true,
	val initialLoadingError: Throwable? = null,
	val transactions: List<FinancialTransaction> = emptyList(),
	val transactionsLoadingMore: Boolean = false,
	val transactionsLoadingMoreError: Throwable? = null,
) {

}