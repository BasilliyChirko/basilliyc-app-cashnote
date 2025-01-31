package basilliyc.cashnote.ui.account.list

import basilliyc.cashnote.data.FinancialAccount

data class AccountListState(
	val content: Content = Content.Loading,
	val dialog: Dialog? = null,
	val action: Action? = null,
) {
	
	sealed interface Content {
		data object Loading : Content
		data object DataEmpty : Content
		data class Data(
			val financialAccounts: List<FinancialAccount>,
		) : Content
	}
	
	sealed interface Dialog {
		data class AccountDeleteConfirmation(val accountId: Long) : Dialog
	}
	
	sealed interface Action {
		data class AccountEdit(val accountId: Long) : Action
		data object AccountDeletionSuccess : Action
		data object AccountDeletionError : Action
		data class AccountHistory(val accountId: Long) : Action
	}
	
}