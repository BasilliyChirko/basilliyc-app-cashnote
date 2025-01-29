package basilliyc.cashnote.ui.account.transaction

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.ui.components.TextFieldState

data class AccountTransactionState(
	val content: Content = Content.Loading,
	val action: Action? = null,
	val dialog: Dialog? = null,
) {
	
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
			val timestamp: Long,
		) : Content
	}
	
	sealed interface Dialog {
		data object AccountDeleteConfirmation : Dialog
		data class DatePicker(val timestamp: Long) : Dialog
		data class TimePicker(val timestamp: Long) : Dialog
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object Cancel : Action
		data object SaveError : Action
		data class AccountEdit(val accountId: Long) : Action
		data object AccountDeletionSuccess : Action
		data object AccountDeletionError : Action
		data class AccountHistory(val accountId: Long) : Action
	}
}