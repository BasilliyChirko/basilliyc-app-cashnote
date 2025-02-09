package basilliyc.cashnote.ui.category.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.flowZip
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryListViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), CategoryListListener {
	
	val route: AppNavigation.CategoryList = savedStateHandle.toRoute()
	
	val state = CategoryListStateHolder()
	
	
	init {
		state.page = CategoryListStateHolder.Page.Loading
		
		viewModelScope.launch {
			flowZip(
				financialManager.getCategoryListAsFlow(),
				financialManager.getCategoryToAccountParamsListAsFlow(),
			) { categories, categoryToAccountParamsList ->
				
				categories.map { category ->
					
					val visibleInAccountsCount = categoryToAccountParamsList
						.filter { it.categoryId == category.id }
						.count { it.visible }
					
					CategoryListStateHolder.CategoryWithCount(
						category = category,
						visibleInAccountsCount = visibleInAccountsCount,
					)
					
				}
				
			}.collectLatest {
				state.page = CategoryListStateHolder.Page.Data(
					categories = it,
					categoriesDragged = null,
				)
			}
		}
	}
	
	override fun onResultHandled() {
		state.result = null
	}
	
	override fun onCategoryClicked(categoryId: Long) {
		state.result = CategoryListStateHolder.Result.NavigateCategoryForm(categoryId)
	}
	
	override fun onCategoryAddClicked() {
		state.result = CategoryListStateHolder.Result.NavigateCategoryForm(null)
	}
	
	override fun onDragStarted() {
		val data = state.pageData ?: return
		state.pageData = data.copy(categoriesDragged = data.categories)
	}
	
	override fun onDragCompleted(from: Int, to: Int) {
		state.pageData ?: return
		schedule {
			financialManager.changeCategoryPosition(from, to)
		}
	}
	
	override fun onDragReverted() {
		val data = state.pageData ?: return
		state.pageData = data.copy(categoriesDragged = null)
	}
	
	override fun onDragMoved(from: Int, to: Int) {
		val data = state.pageData ?: return
		state.pageData = data.copy(categoriesDragged = data.categoriesDragged?.let { ArrayList(it) }
			?.reordered(from, to))
	}
	
	
}