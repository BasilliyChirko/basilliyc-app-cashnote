package basilliyc.cashnote.ui.account.transaction.form

import android.icu.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.ui.account.transaction.form.TransactionFormState.Page
import basilliyc.cashnote.ui.activity.AppNavigation
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.toPriceWithCoins
import kotlinx.coroutines.launch

class TransactionFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	private val financialManager: FinancialManager by inject()
	
	val route: AppNavigation.TransactionForm = savedStateHandle.toRoute()
	var state by mutableStateOf(TransactionFormState())
		private set
	
	private var statePageData
		get() = state.page as? Page.Data
		set(value) {
			if (value != null) state = state.copy(page = value)
		}
	private var stateDialog
		get() = state.dialog
		set(value) {
			state = state.copy(dialog = value)
		}
	
	lateinit var account: FinancialAccount
	lateinit var category: FinancialCategory
	
	init {
		state = state.copy(page = Page.Loading)
		
		viewModelScope.launch {
			
			account = financialManager.getAccountById(route.accountId)
				?: throw Throwable("Account with id ${route.accountId} not found")
			
			category = financialManager.getCategoryById(route.categoryId)
				?: throw Throwable("Category with id ${route.categoryId} not found")
			
			val transaction = route.transactionId?.let {
				financialManager.getTransactionById(it)
			}
			
			val deviation = transaction?.value ?: 0.0
			
			statePageData = Page.Data(
				account = account,
				category = category,
				isNew = transaction == null,
				isInputDeviation = true,
				timeInMillis = transaction?.date ?: System.currentTimeMillis(),
				balanceWithoutDeviation = account.balance - deviation,
				comment = TextFieldState(transaction?.comment ?: ""),
				deviation = deviation,
				deviationTextState = TextFieldState(
					value = transaction?.value?.toPriceWithCoins(false) ?: "",
					error = null,
				),
				balanceTextState = TextFieldState(
					value = account.balance.toPriceWithCoins(false),
					error = null,
				)
			)
			
		}
	}
	
	private fun stringPriceCorrection(string: String): String {
		var string = string.replace(',', '.').trim()
		
		if (string.indexOfLast { it == '-' } > 0) {
			string = "-" + string.replace("-", "")
		}
		
		string.split('.').let { array ->
			if (array.size > 2) {
				string = array[0] + "." + array[1]
			}
		}
		
		string.split('.').let { array ->
			if ((array.getOrNull(1)?.length ?: 0) > 2) {
				string = array.getOrNull(0) + "." + array.getOrNull(1)?.substring(0, 2)
			}
		}
		
		return string
	}
	
	fun onDeviationChanged(string: String) {
		val data = statePageData ?: return
		
		val deviationString = stringPriceCorrection(string)
		val deviationDouble = deviationString.toDoubleOrNull() ?: 0.0
		val balanceDouble = data.balanceWithoutDeviation + deviationDouble
		
		statePageData = data.copy(
			deviation = deviationDouble,
			deviationTextState = TextFieldState(deviationString),
			balanceTextState = TextFieldState(balanceDouble.toPriceWithCoins(false)),
		)
	}
	
	fun onBalanceChanged(string: String) {
		val data = statePageData ?: return
		
		val balanceString = stringPriceCorrection(string)
		val balanceDouble = balanceString.toDoubleOrNull() ?: 0.0
		val deviationDouble = balanceDouble - data.balanceWithoutDeviation
		
		statePageData = data.copy(
			deviation = deviationDouble,
			deviationTextState = TextFieldState(deviationDouble.toPriceWithCoins(false)),
			balanceTextState = TextFieldState(balanceString),
		)
	}
	
	fun onFocusChanged(focus: TransactionFormState.Focus) {
		statePageData = statePageData?.copy(
			focusedField = focus
		)
	}
	
	fun onInputTypeChanged(newInputDeviation: Boolean) {
		val data = statePageData ?: return
		val previousInputType = data.isInputDeviation
		if (newInputDeviation == previousInputType) return
		
		if (newInputDeviation) {
			
			statePageData = data.copy(
				isInputDeviation = newInputDeviation,
				deviationTextState = TextFieldState(
					data.deviation.takeIf { it != 0.0 }?.toPriceWithCoins(false) ?: ""
				)
			)
			
		} else {
			
			statePageData = data.copy(
				isInputDeviation = newInputDeviation,
				deviationTextState = TextFieldState(
					(data.balanceWithoutDeviation + data.deviation).toPriceWithCoins(
						false
					)
				)
			)
			
		}

//		statePageData = data.copy(
//			deviation = 0.0,
//			isInputDeviation = newInputDeviation,
//			input = TextFieldState("")
//		)

//		statePageData = when {
//			previousInputType == TransactionType.Balance -> data.copy(
//				type = type,
//				deviation = 0.0,
//				input = TextFieldState("")
//			)
//
//			type == TransactionType.Balance -> data.copy(
//				type = type,
//				deviation = 0.0,
//				input = TextFieldState(data.balanceWithoutDeviation.toPriceWithCoins())
//			)
//
//			else -> data.copy(
//				type = type,
//				deviation = data.deviation * -1,
//				input = TextFieldState(data.balanceWithoutDeviation.toPriceWithCoins())
//			)
//		}
	}
	
	fun onCommentChanged(comment: String) {
		statePageData = statePageData?.copy(
			comment = TextFieldState(comment),
		)
	}
	
	
	fun onDateClicked() {
		val data = statePageData ?: return
		stateDialog = TransactionFormState.Dialog.DatePicker(data.timeInMillis)
	}
	
	fun onDialogDateSelected(timeInMillis: Long) {
		val data = statePageData ?: return
		
		val previousCalendar =
			Calendar.getInstance().apply { this.timeInMillis = data.timeInMillis }
		val newCalendar = Calendar.getInstance().apply {
			this.timeInMillis = timeInMillis
			set(Calendar.HOUR_OF_DAY, previousCalendar.get(Calendar.HOUR_OF_DAY))
			set(Calendar.MINUTE, previousCalendar.get(Calendar.MINUTE))
			set(Calendar.SECOND, previousCalendar.get(Calendar.SECOND))
		}
		
		statePageData = statePageData?.copy(
			timeInMillis = newCalendar.timeInMillis,
		)
		stateDialog = null
	}
	
	fun onDialogDateDismiss() {
		stateDialog = null
	}
	
	fun onTimeClicked() {
		val data = statePageData ?: return
		stateDialog = TransactionFormState.Dialog.TimePicker(data.timeInMillis)
	}
	
	fun onDialogTimeSelected(hour: Int, minute: Int) {
		val data = statePageData ?: return
		
		val newCalendar = Calendar.getInstance().apply {
			timeInMillis = data.timeInMillis
			set(Calendar.HOUR_OF_DAY, hour)
			set(Calendar.MINUTE, minute)
		}
		
		statePageData = statePageData?.copy(
			timeInMillis = newCalendar.timeInMillis
		)
		stateDialog = null
	}
	
	fun onDialogTimeDismiss() {
		stateDialog = null
	}
	
}