package basilliyc.cashnote.ui.transaction.category.form

import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.data.FinancialTransactionCategoryIcon
import basilliyc.cashnote.ui.components.TextFieldState

data class TransactionCategoryFormState(
	val content: Content = Content.Loading,
	val action: Action? = null,
) {
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val isNew: Boolean,
			val name: TextFieldState,
			val icon: FinancialTransactionCategoryIcon?,
		) : Content {
			constructor(category: FinancialTransactionCategory) : this(
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