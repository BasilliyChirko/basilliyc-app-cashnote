package basilliyc.cashnote.ui.account.transaction.form_old

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.ui.components.TextFieldState

data class AccountTransactionFormState(
	val content: Content = Content.Loading,
	val action: Action? = null,
	val dialog: Dialog? = null,
) {
	
	sealed interface Content {
		data object Loading : Content
		data class Data(
			val isNew: Boolean,
			val financialAccount: FinancialAccount,
			val isBalanceReduce: Boolean? = null,
			val balanceDifference: TextFieldState,
			val balanceNew: TextFieldState,
			val comment: TextFieldState,
			val availableCategories: List<FinancialCategory>,
			val selectedCategoryId: Long?,
			val timestamp: Long,
		) : Content
	}
	
	sealed interface Dialog {
		data class DatePicker(val timestamp: Long) : Dialog
		data class TimePicker(val timestamp: Long) : Dialog
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object Cancel : Action
		data object SaveError : Action
	}
}