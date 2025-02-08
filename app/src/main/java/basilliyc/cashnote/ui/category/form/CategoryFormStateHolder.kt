package basilliyc.cashnote.ui.category.form

import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialIcon
import basilliyc.cashnote.ui.components.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CategoryFormStateHolder(
	page: Page = Page.Loading,
	result: Result? = null,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page as? Page.Data
		set(value) =
			if (value != null) page = value
			else Unit
	
	var pageDataName
		get() = pageData?.name
		set(value) =
			if (value != null) pageData = pageData?.copy(name = value)
			else Unit
	
	var result by mutableStateOf(result)
	
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val isNew: Boolean,
			val name: TextFieldState,
			val icon: FinancialIcon?,
			val color: FinancialColor?,
		) : Page {
			constructor(category: FinancialCategory) : this(
				isNew = category.id == 0L,
				name = TextFieldState(value = category.name),
				icon = category.icon,
				color = category.color,
			)
		}
	}
	
	sealed interface Result {
		data class SaveSuccess(val isNew: Boolean) : Result
		data object SaveError : Result
		data object NavigateBack : Result
		data class NavigateCategoryExtendedDeletion(val categoryId: Long) : Result
		data object DeleteSuccess : Result
		data object DeleteError : Result
	}
	
}