package basilliyc.cashnote.ui.account.details

interface AccountDetailsListener {
	fun onResultConsumed()
	fun onCategoryClicked(id: Long)
	fun onAccountCategoriesClicked()
	fun onAccountEditClicked()
	fun onAccountHistoryClicked()
	fun onAccountParamsClicked()
	fun onAccountDeleteClicked()
	fun onDeleteAccountConfirmed()
	fun onDeleteAccountCanceled()
}