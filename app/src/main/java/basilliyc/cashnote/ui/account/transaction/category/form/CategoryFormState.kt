package basilliyc.cashnote.ui.account.transaction.category.form

import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCategoryIcon
import basilliyc.cashnote.ui.components.TextFieldState

data class CategoryFormState(
	val content: Content = Content.Loading,
	val action: Action? = null,
) {
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val isNew: Boolean,
			val name: TextFieldState,
			val icon: FinancialCategoryIcon?,
		) : Content {
			constructor(category: FinancialCategory) : this(
				isNew = category.id == 0L,
				name = TextFieldState(value = category.name),
				icon = category.icon,
			)
		}
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object Cancel : Action
		data object SaveError : Action
		data object DeleteSuccess : Action
		data object DeleteError : Action
	}
	
}