package basilliyc.cashnote.ui.category.deletion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.category.deletion.CategoryExtendedDeletionStateHolder.Page
import kotlinx.coroutines.launch

class CategoryExtendedDeletionViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), CategoryExtendedDeletionListener {
	
	val route: AppNavigation.CategoryExtendedDeletion = savedStateHandle.toRoute()
	
	val state = CategoryExtendedDeletionStateHolder()
	
	init {
		viewModelScope.launch {
			state.page = Page.Loading
			
			val category = financialManager.requireCategoryById(route.categoryId)
			val categories = financialManager.getCategoryList().filter {
				it.id != category.id
			}
			val extendedResult = financialManager.checkExtendedDeletionForCategory(route.categoryId)
			
			state.page = Page.Data(
				category = category,
				categoryExtendedDeletionResult = extendedResult,
				deletionStrategy = CategoryExtendedDeletionStateHolder.DeletionStrategy.DeleteTransactions,
				strategyDeleteTransactionsAffectAccounts = false,
				strategyChangeTransactionsCategoryTargetCategory = null,
				strategyChangeTransactionsCategoryAvailableCategories = categories,
			)
		}
	}
	
	override fun onResultConsumed() {
		state.result = null
	}
	
	override fun onDeletionStrategyChanged(strategy: CategoryExtendedDeletionStateHolder.DeletionStrategy) {
		state.pageData = state.pageData?.copy(
			deletionStrategy = strategy,
		)
	}
	
	override fun onStrategyDeleteTransactionsAffectAccountsChanged(affect: Boolean) {
		state.pageData = state.pageData?.copy(
			strategyDeleteTransactionsAffectAccounts = affect,
		)
	}
	
	override fun onStrategyChangeTransactionsCategoryTargetCategoryChanged(category: FinancialCategory?) {
		state.pageData = state.pageData?.copy(
			strategyChangeTransactionsCategoryTargetCategory = category,
		)
	}
	
	override fun onDeleteClicked() {
		val pageData = state.pageData ?: return
		
		when (pageData.deletionStrategy) {
			CategoryExtendedDeletionStateHolder.DeletionStrategy.ChangeTransactionsCategory -> {
				
				val targetCategory = pageData.strategyChangeTransactionsCategoryTargetCategory
				if (targetCategory == null) {
					state.result =
						CategoryExtendedDeletionStateHolder.Result.DeletionTargetCategoryRequired
					return
				}
				
				schedule {
					try {
						financialManager.deleteCategoryExtended(
							categoryId = route.categoryId,
							strategy = FinancialManager.DeleteCategoryExtendedStrategy.ChangeTransactionsCategory(
								targetCategory = targetCategory
							)
						)
						state.result = CategoryExtendedDeletionStateHolder.Result.DeletionSuccess
					} catch (t: Throwable) {
						state.result = CategoryExtendedDeletionStateHolder.Result.DeletionError
					}
				}
			}
			
			CategoryExtendedDeletionStateHolder.DeletionStrategy.DeleteTransactions -> {
				
				schedule {
					try {
						financialManager.deleteCategoryExtended(
							categoryId = route.categoryId,
							strategy = FinancialManager.DeleteCategoryExtendedStrategy.DeleteTransactions(
								affectAccounts = pageData.strategyDeleteTransactionsAffectAccounts
							)
						)
						state.result = CategoryExtendedDeletionStateHolder.Result.DeletionSuccess
					} catch (t: Throwable) {
						state.result = CategoryExtendedDeletionStateHolder.Result.DeletionError
					}
				}
			}
			
		}
	}
	
}