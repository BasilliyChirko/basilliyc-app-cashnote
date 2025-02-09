package basilliyc.cashnote.ui.category.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.data.FinancialCategory

class CategoryListStateHolder(
	page: Page = Page.Loading,
	result: Result? = null,
) {
	
	var page by mutableStateOf(page)
	
	var pageData: Page.Data?
		get() = page as? Page.Data
		set(value) =
			if (value != null) page = value
			else Unit
	
	var result by mutableStateOf(result)
	
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val categories: List<CategoryWithCount>,
			val categoriesDragged: List<CategoryWithCount>?,
		) : Page
	}
	
	data class CategoryWithCount(
		val category: FinancialCategory,
		val visibleInAccountsCount: Int,
	)
	
	sealed interface Result {
		data class NavigateCategoryForm(val categoryId: Long?) : Result
	}
	
}