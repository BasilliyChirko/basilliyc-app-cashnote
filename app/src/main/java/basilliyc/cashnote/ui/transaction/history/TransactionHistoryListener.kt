package basilliyc.cashnote.ui.transaction.history

import basilliyc.cashnote.ui.base.BaseListener

interface TransactionHistoryListener : BaseListener {
	fun onInitialLoadingErrorSubmitted()
	fun onTransactionsLoadingMoreErrorSubmitted()
	fun onTransactionsLoadingMore()
	fun onTransactionEditClicked(transactionId: Long)
	fun onTransactionDeleteClicked(transactionId: Long)
}