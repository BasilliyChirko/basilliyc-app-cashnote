package basilliyc.cashnote.ui.account.transaction

import android.icu.util.Calendar
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
import basilliyc.cashnote.utils.toPriceWithCoins
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountTransactionViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	init {
		defaultEventSkipIfBusy = true
		defaultEventPostDelay = true
	}
	
	private val financialManager: FinancialManager by inject()
	
	private val route = savedStateHandle.toRoute<AppNavigation.AccountTransaction>()
	
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
		viewModelScope.launch {
			
			val account = financialManager.getAccountById(route.accountId)
				?: throw IllegalStateException("Account with id ${route.accountId} is not present in database")
			
			val availableCategories = financialManager.getAvailableTransactionCategories()
			
			state = state.copy(
				content = AccountTransactionState.Content.Data(
					financialAccount = account,
					balanceDifference = TextFieldState(""),
					balanceNew = TextFieldState(account.balance.toPriceWithCoins()),
					comment = TextFieldState(""),
					availableCategories = availableCategories,
					selectedCategoryId = null,
					timestamp = System.currentTimeMillis(),
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
		viewModelScope.launch {
			financialManager.getAccountByIdAsFlow(route.accountId).collectLatest { account ->
				if (account != null) {
					stateContentData = stateContentData?.copy(
						financialAccount = account
					)
				}
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
			balanceNew = TextFieldState(balanceNewValue.toPriceWithCoins()),
			isBalanceReduce = (balanceDifferenceValue < 0).takeIf { balanceNewValue != account.balance },
		)
	}
	
	fun onBalanceNewChanged(balanceNewString: String) {
		val account = stateContentData?.financialAccount ?: return
		
		val balanceNewString = balanceNewString.replace(",", ".")
		val balanceNewValue = balanceNewString.toDoubleOrNull() ?: 0.0
		val balanceDifference = balanceNewValue - account.balance
		
		stateContentData = stateContentData?.copy(
			balanceDifference = TextFieldState(balanceDifference.toPriceWithCoins()),
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
					timestamp = data.timestamp,
				)
				stateAction = AccountTransactionState.Action.SaveSuccess
			} catch (t: Throwable) {
				logcat.error(t)
				stateAction = AccountTransactionState.Action.SaveError
			}
		}
		
	}
	
	fun onCancelClicked() = scheduleEvent {
		stateAction = AccountTransactionState.Action.Cancel
	}
	
	fun onAccountEditClicked() = scheduleEvent {
		val data = stateContentData ?: return@scheduleEvent
		stateAction = AccountTransactionState.Action.AccountEdit(data.financialAccount.id)
	}
	
	fun onAccountDeleteClicked() {
		stateDialog = AccountTransactionState.Dialog.AccountDeleteConfirmation
	}
	
	fun onAccountHistoryClicked() = scheduleEvent {
		val data = stateContentData ?: return@scheduleEvent
		stateAction = AccountTransactionState.Action.AccountHistory(data.financialAccount.id)
	}
	
	fun onAccountDeleteDialogCanceled() {
		if (stateDialog != AccountTransactionState.Dialog.AccountDeleteConfirmation) return
		stateDialog = null
	}
	
	fun onAccountDeleteDialogConfirmed() {
		if (stateDialog != AccountTransactionState.Dialog.AccountDeleteConfirmation) return
		stateDialog = null
		
		
		scheduleEvent {
			try {
				financialManager.deleteAccount(route.accountId)
				stateAction = AccountTransactionState.Action.AccountDeletionSuccess
			} catch (t: Throwable) {
				logcat.error(t)
				stateAction = AccountTransactionState.Action.AccountDeletionError
			}
		}
	}
	
	fun onDateClicked() {
		val data = stateContentData ?: return
		stateDialog = AccountTransactionState.Dialog.DatePicker(data.timestamp)
	}
	
	fun onDialogDateSelected(timestamp: Long) {
		val data = stateContentData ?: return
		
		val previousCalendar = Calendar.getInstance().apply { timeInMillis = data.timestamp }
		val newCalendar = Calendar.getInstance().apply {
			timeInMillis = timestamp
			set(Calendar.HOUR_OF_DAY, previousCalendar.get(Calendar.HOUR_OF_DAY))
			set(Calendar.MINUTE, previousCalendar.get(Calendar.MINUTE))
			set(Calendar.SECOND, previousCalendar.get(Calendar.SECOND))
		}
		
		stateContentData = stateContentData?.copy(
			timestamp = newCalendar.timeInMillis,
		)
		stateDialog = null
	}
	
	fun onDialogDateDismiss() {
		stateDialog = null
	}
	
	fun onTimeClicked() {
		val data = stateContentData ?: return
		stateDialog = AccountTransactionState.Dialog.TimePicker(data.timestamp)
	}
	
	fun onDialogTimeSelected(hour: Int, minute: Int) {
		val data = stateContentData ?: return
		
		val newCalendar = Calendar.getInstance().apply {
			timeInMillis = data.timestamp
			set(Calendar.HOUR_OF_DAY, hour)
			set(Calendar.MINUTE, minute)
		}
		
		stateContentData = stateContentData?.copy(
			timestamp = newCalendar.timeInMillis
		)
		stateDialog = null
	}
	
	fun onDialogTimeDismiss() {
		stateDialog = null
	}
	
}
