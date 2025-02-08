package basilliyc.cashnote.ui.category.deletion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.backend.manager.FinancialManager.CategoryExtendedDeletionResult
import basilliyc.cashnote.data.FinancialCategory

class CategoryExtendedDeletionStateHolder(
	page: Page = Page.Loading,
	result: Result? = null,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page as? Page.Data
		set(value) =
			if (value != null) page = value
			else Unit
	
	var result by mutableStateOf(result)
	
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val category: FinancialCategory,
			val categoryExtendedDeletionResult: CategoryExtendedDeletionResult,
			val deletionStrategy: DeletionStrategy,
			val strategyDeleteTransactionsAffectAccounts: Boolean,
			val strategyChangeTransactionsCategoryTargetCategory: FinancialCategory?,
			val strategyChangeTransactionsCategoryAvailableCategories: List<FinancialCategory>,
		) : Page
	}
	
	enum class DeletionStrategy {
		ChangeTransactionsCategory, DeleteTransactions
	}
	
	sealed interface Result {
		data object DeletionSuccess : Result
		data object DeletionError : Result
		data object DeletionTargetCategoryRequired : Result
	}
	
}