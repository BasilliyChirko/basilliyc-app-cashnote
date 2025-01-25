package basilliyc.cashnote.ui.account.transaction

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.ui.components.TextFieldState

object AccountTransactionState {
	
	data class Page(
		val content: Content = Content.Loading,
		val action: Action? = null,
	)
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val financialAccount: FinancialAccount,
			val isBalanceReduce: Boolean? = null,
			val balanceDifference: TextFieldState,
			val balanceNew: TextFieldState,
			val comment: TextFieldState,
			val availableCategories: List<FinancialTransactionCategory>,
			val selectedCategoryId: Long?,
		) : Content
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object Cancel : Action
		data object SaveError : Action
	}
}