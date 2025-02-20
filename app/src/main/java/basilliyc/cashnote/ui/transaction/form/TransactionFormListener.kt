package basilliyc.cashnote.ui.transaction.form

import basilliyc.cashnote.data.FinancialCategory

interface TransactionFormListener {
	fun onActionConsumed() {}
	fun onBalanceChanged(balanceString: String) {}
	fun onDeviationChanged(deviationString: String) {}
	fun onCommentChanged(comment: String) {}
	fun onFocusChanged(focus: TransactionFormState.Focus) {}
	fun onDateClicked() {}
	fun onDialogDateSelected(timeInMillis: Long) {}
	fun onDialogDateDismiss() {}
	fun onTimeClicked() {}
	fun onDialogTimeSelected(hour: Int, minute: Int) {}
	fun onDialogTimeDismiss() {}
	fun onSaveClicked() {}
	fun onCategoryChanged(category: FinancialCategory) {}
}