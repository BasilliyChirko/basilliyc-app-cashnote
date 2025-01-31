package basilliyc.cashnote.ui.account.transaction.form

import android.icu.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.toPriceWithCoins
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountTransactionFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	init {
		defaultEventSkipIfBusy = true
		defaultEventPostDelay = true
	}
	
	private val financialManager: FinancialManager by inject()
	
	private val route = savedStateHandle.toRoute<AppNavigation.AccountTransactionForm>()
	
	//----------------------------------------------------------------------------------------------
	//  State declaration
	//----------------------------------------------------------------------------------------------
	
	var state by mutableStateOf(AccountTransactionFormState())
		private set
	private var stateContentData
		get() = state.content as? AccountTransactionFormState.Content.Data
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
	
	private fun updateContentData(call: AccountTransactionFormState.Content.Data.() -> AccountTransactionFormState.Content.Data) {
		val content = stateContentData
		if (content is AccountTransactionFormState.Content.Data) {
			stateContentData = stateContentData?.call()
		}
	}
	
	//----------------------------------------------------------------------------------------------
	//  Initialization of the state
	//----------------------------------------------------------------------------------------------
	
	init {
		state = state.copy(content = AccountTransactionFormState.Content.Loading)
		viewModelScope.launch {
			
			val account = financialManager.getAccountById(route.accountId)
				?: throw IllegalStateException("Account with id ${route.accountId} is not present in database")
			
			val availableCategories = financialManager.getAvailableTransactionCategories()
			
			val transaction = route.transactionId?.let {
				financialManager.getTransactionById(it)
			}
			
			state = state.copy(
				content = AccountTransactionFormState.Content.Data(
					financialAccount = account,
					balanceDifference = TextFieldState(
						value = transaction?.value?.toPriceWithCoins() ?: ""
					),
					balanceNew = TextFieldState(
						value = account.balance.toPriceWithCoins()
					),
					comment = TextFieldState(
						value = transaction?.comment ?: ""
					),
					availableCategories = availableCategories,
					selectedCategoryId = transaction?.categoryId,
					timestamp = transaction?.date ?: System.currentTimeMillis(),
					isNew = transaction == null
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
				financialManager.saveTransaction(
					FinancialTransaction(
						id = route.transactionId ?: 0L,
						accountId = account.id,
						value = transactionValue,
						comment = data.comment.value.takeIf { it.isNotBlank() },
						categoryId = data.selectedCategoryId,
						date = data.timestamp,
					)
				)
				stateAction = AccountTransactionFormState.Action.SaveSuccess
			} catch (t: Throwable) {
				logcat.error(t)
				stateAction = AccountTransactionFormState.Action.SaveError
			}
		}
		
	}
	
	fun onCancelClicked() = scheduleEvent {
		stateAction = AccountTransactionFormState.Action.Cancel
	}
	
	fun onDateClicked() {
		val data = stateContentData ?: return
		stateDialog = AccountTransactionFormState.Dialog.DatePicker(data.timestamp)
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
		stateDialog = AccountTransactionFormState.Dialog.TimePicker(data.timestamp)
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
