package basilliyc.cashnote.ui.category.deletion

import basilliyc.cashnote.data.FinancialCategory

interface CategoryExtendedDeletionListener {
	fun onResultConsumed()
	fun onDeletionStrategyChanged(strategy: CategoryExtendedDeletionStateHolder.DeletionStrategy)
	fun onStrategyDeleteTransactionsAffectAccountsChanged(affect: Boolean)
	fun onStrategyChangeTransactionsCategoryTargetCategoryChanged(category: FinancialCategory?)
	fun onDeleteClicked()
}