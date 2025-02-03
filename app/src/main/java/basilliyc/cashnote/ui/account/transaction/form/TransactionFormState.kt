package basilliyc.cashnote.ui.account.transaction.form

import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.ui.components.TextFieldState

data class TransactionFormState(
	val page: Page = Page.Loading,
	val dialog: Dialog? = null,
) {
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val account: FinancialAccount,
			val category: FinancialCategory,
			val isNew: Boolean,
			val isInputDeviation: Boolean,
			val timeInMillis: Long,
			val input: TextFieldState,
			val deviation: Double,
			val balanceWithoutDeviation: Double,
			val comment: TextFieldState,
		) : Page
	}
	
	sealed interface Dialog {
		data class DatePicker(val timestamp: Long) : Dialog
		data class TimePicker(val timestamp: Long) : Dialog
	}
	
}