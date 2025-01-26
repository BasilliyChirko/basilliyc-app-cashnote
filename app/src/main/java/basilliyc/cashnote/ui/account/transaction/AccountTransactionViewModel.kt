package basilliyc.cashnote.ui.account.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.asPriceWithCoins
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountTransactionViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	private val financialManager: FinancialManager by inject()
	
	//----------------------------------------------------------------------------------------------
	//  State declaration
	//----------------------------------------------------------------------------------------------
	
	var state by mutableStateOf(AccountTransactionState())
		private set
	private var stateContentData
		get() = state.content as? AccountTransactionState.Content.Data
		set(value) {
			if (value != null) state = state.copy(content = value)
		}
	private var stateDialog
		get() = state.dialog
		set(value) {
			state = state.copy(dialog = value)
		}
	private var stateAction
		get() = state.action
		set(value) {
			state = state.copy(action = value)
		}
	
	private fun updateContentData(call: AccountTransactionState.Content.Data.() -> AccountTransactionState.Content.Data) {
		val content = stateContentData
		if (content is AccountTransactionState.Content.Data) {
			stateContentData = stateContentData?.call()
		}
	}
	
	//----------------------------------------------------------------------------------------------
	//  Initialization of the state
	//----------------------------------------------------------------------------------------------
	
	init {
		state = state.copy(content = AccountTransactionState.Content.Loading)
		val route = savedStateHandle.toRoute<AppNavigation.AccountTransaction>()
		viewModelScope.launch {
			
			val account = financialManager.getAccountById(route.accountId)
				?: throw IllegalStateException("Account with id ${route.accountId} is not present in database")
			
			val availableCategories = financialManager.getAvailableTransactionCategories()
			
			state = state.copy(
				content = AccountTransactionState.Content.Data(
					financialAccount = account,
					balanceDifference = TextFieldState(""),
					balanceNew = TextFieldState(account.balance.asPriceWithCoins()),
					comment = TextFieldState(""),
					availableCategories = availableCategories,
					selectedCategoryId = null,
				)
			)
		}
	}
	
	init {
		viewModelScope.launch {
			financialManager.getAvailableTransactionCategoriesAsFlow().collectLatest { categories ->
				stateContentData = stateContentData?.copy(
					availableCategories = categories
				)
			}
		}
	}
	
	//----------------------------------------------------------------------------------------------
	//  Reaction methods
	//----------------------------------------------------------------------------------------------
	
	fun onActionConsumed() {
		stateAction = null
	}
	
	fun onBalanceDifferenceChanged(balanceDifferenceString: String) {
		val account = stateContentData?.financialAccount ?: return
		
		val balanceDifferenceString = balanceDifferenceString.replace(",", ".")
		
		val balanceDifferenceValue = balanceDifferenceString.toDoubleOrNull() ?: 0.0
		
		val balanceNewValue = account.balance + balanceDifferenceValue
		
		stateContentData = stateContentData?.copy(
			balanceDifference = TextFieldState(balanceDifferenceString),
			balanceNew = TextFieldState(balanceNewValue.asPriceWithCoins()),
			isBalanceReduce = (balanceDifferenceValue < 0).takeIf { balanceNewValue != account.balance },
		)
	}
	
	fun onBalanceNewChanged(balanceNewString: String) {
		val account = stateContentData?.financialAccount ?: return
		
		val balanceNewString = balanceNewString.replace(",", ".")
		val balanceNewValue = balanceNewString.toDoubleOrNull() ?: 0.0
		val balanceDifference = balanceNewValue - account.balance
		
		stateContentData = stateContentData?.copy(
			balanceDifference = TextFieldState(balanceDifference.asPriceWithCoins()),
			balanceNew = TextFieldState(balanceNewString),
			isBalanceReduce = (balanceDifference < 0).takeIf { balanceNewValue != account.balance },
		)
	}
	
	fun onCommentChanged(comment: String) {
		stateContentData = stateContentData?.copy(
			comment = TextFieldState(comment),
		)
	}
	
	fun onCategoryChanged(categoryId: Long?) {
		stateContentData = stateContentData?.copy(
			selectedCategoryId = categoryId,
		)
	}
	
	fun onSaveClicked() {
		val data = stateContentData ?: return
		val account = data.financialAccount
		
		val transactionValue = data.balanceDifference.value.toDoubleOrNull()
		
		if (transactionValue == null) {
			updateContentData {
				copy(
					balanceDifference = balanceDifference.copy(
						error = TextFieldError.IncorrectValue
					)
				)
			}
			return
		}
		
		scheduleEvent(
			skipIfBusy = true,
			postDelay = true,
		) {
			try {
				financialManager.createTransaction(
					accountId = account.id,
					value = transactionValue,
					comment = data.comment.value,
					categoryId = data.selectedCategoryId,
				)
				stateAction = AccountTransactionState.Action.SaveSuccess
			} catch (t: Throwable) {
				logcat.error(t)
				stateAction = AccountTransactionState.Action.SaveError
			}
		}
		
	}
	
	fun onCancelClicked() = scheduleEvent(skipIfBusy = true, postDelay = true) {
		stateAction = AccountTransactionState.Action.Cancel
	}
	
	fun onAccountEditClicked() = scheduleEvent(skipIfBusy = true, postDelay = true) {
		val data = stateContentData ?: return@scheduleEvent
		stateAction = AccountTransactionState.Action.AccountEdit(data.financialAccount.id)
	}
	
	fun onAccountDeleteClicked() {
		stateDialog = AccountTransactionState.Dialog.AccountDeleteConfirmation
	}
	
	fun onAccountHistoryClicked() = scheduleEvent(skipIfBusy = true, postDelay = true) {
		val data = stateContentData ?: return@scheduleEvent
		stateAction = AccountTransactionState.Action.AccountHistory(data.financialAccount.id)
	}
	
	fun onAccountDeleteDialogCanceled() {
		//TODO implement
	}
	
	fun onAccountDeleteDialogConfirmed() {
		//TODO implement
	}
	
	
}
