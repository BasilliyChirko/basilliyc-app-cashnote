package basilliyc.cashnote.ui.account.transaction.form

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.ui.components.TextFieldState

data class TransactionFormState(
	val page: Page = Page.Loading,
	val dialog: Dialog? = null,
	val action: Action? = null,
) {
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val account: FinancialAccount,
			val category: FinancialCategory,
			val isNew: Boolean,
			val isInputDeviation: Boolean,
			val timeInMillis: Long,
			val timeInMillisOriginal: Long = timeInMillis,
			val deviationTextState: TextFieldState,
			val deviationTextPlaceholder: String,
			val balanceTextState: TextFieldState,
			val balanceTextPlaceholder: String,
			val deviation: Double,
			val balanceWithoutDeviation: Double,
			val comment: TextFieldState,
			val focusedField: Focus = Focus.Deviation,
		) : Page
	}
	
	enum class Focus {
		Deviation, Balance, Comment,
	}
	
	sealed interface Dialog {
		data class DatePicker(val timestamp: Long) : Dialog
		data class TimePicker(val timestamp: Long) : Dialog
	}
	
	sealed interface Action {
		data object SaveSuccess : Action
		data object SaveError : Action
		data object DeviationCantBeZero : Action
	}
	
}