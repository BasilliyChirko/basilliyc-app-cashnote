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
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.ui.account.transaction.form.TransactionFormState.Action
import basilliyc.cashnote.ui.account.transaction.form.TransactionFormState.Page
import basilliyc.cashnote.ui.activity.AppNavigation
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.toPriceWithCoins
import kotlinx.coroutines.launch

class TransactionFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), TransactionFormListener {
	
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
	
	private var stateAction
		get() = state.action
		set(value) {
			state = state.copy(action = value)
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
			
			val timeInMillis = transaction?.date ?: System.currentTimeMillis()
			statePageData = Page.Data(
				account = account,
				category = category,
				isNew = transaction == null,
				isInputDeviation = true,
				timeInMillis = timeInMillis,
				timeInMillisOriginal = timeInMillis,
				balanceWithoutDeviation = account.balance - deviation,
				comment = TextFieldState(transaction?.comment ?: ""),
				deviation = deviation,
				deviationTextState = TextFieldState(
					value = transaction?.value?.toPriceWithCoins(false) ?: "",
					error = null,
				),
				deviationTextPlaceholder = transaction?.value?.toPriceWithCoins(false) ?: "0",
				balanceTextState = TextFieldState(
					value = account.balance.toPriceWithCoins(false),
					error = null,
				),
				balanceTextPlaceholder = account.balance.toPriceWithCoins(false)
			)
			
		}
	}
	
	override fun onActionConsumed() {
		state = state.copy(action = null)
	}
	
	override fun onDeviationChanged(string: String) {
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
	
	override fun onBalanceChanged(string: String) {
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
	
	override fun onFocusChanged(focus: TransactionFormState.Focus) {
		val data = statePageData ?: return
		when (focus) {
			TransactionFormState.Focus.Deviation -> {
				statePageData = statePageData?.copy(
					focusedField = focus,
					deviationTextPlaceholder = data.deviation.toPriceWithCoins(false),
					deviationTextState = TextFieldState(""),
				)
			}
			
			TransactionFormState.Focus.Balance -> {
				statePageData = statePageData?.copy(
					focusedField = focus,
					balanceTextPlaceholder = data.balanceWithoutDeviation.plus(data.deviation)
						.toPriceWithCoins(false),
					balanceTextState = TextFieldState(""),
				)
			}
			
			TransactionFormState.Focus.Comment -> {
				statePageData = statePageData?.copy(
					focusedField = focus
				)
			}
		}
	}
	
	override fun onCommentChanged(comment: String) {
		statePageData = statePageData?.copy(
			comment = TextFieldState(comment),
		)
	}
	
	override fun onDateClicked() {
		val data = statePageData ?: return
		stateDialog = TransactionFormState.Dialog.DatePicker(data.timeInMillis)
	}
	
	override fun onDialogDateSelected(timeInMillis: Long) {
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
	
	override fun onDialogDateDismiss() {
		stateDialog = null
	}
	
	override fun onTimeClicked() {
		val data = statePageData ?: return
		stateDialog = TransactionFormState.Dialog.TimePicker(data.timeInMillis)
	}
	
	override fun onDialogTimeSelected(hour: Int, minute: Int) {
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
	
	override fun onDialogTimeDismiss() {
		stateDialog = null
	}
	
	override fun onSaveClicked() {
		val data = statePageData ?: return
		val account = data.account
		
		if (data.deviation == 0.0) {
			when (data.focusedField) {
				TransactionFormState.Focus.Deviation -> {
					statePageData = data.copy(
						deviationTextState = data.deviationTextState.copy(
							error = TextFieldError.DeviationCantBeZero
						)
					)
				}
				
				TransactionFormState.Focus.Balance -> {
					statePageData = data.copy(
						balanceTextState = data.balanceTextState.copy(
							error = TextFieldError.DeviationCantBeZero
						)
					)
				}
				
				TransactionFormState.Focus.Comment -> {
					stateAction = Action.DeviationCantBeZero
				}
			}
			return
		}
		
		schedule(
			skipIfBusy = true,
			postDelay = true,
		) {
			try {
				
				financialManager.saveTransaction(
					transaction = FinancialTransaction(
						id = route.transactionId ?: 0L,
						accountId = account.id,
						value = data.deviation,
						comment = data.comment.value.takeIf { it.isNotBlank() },
						categoryId = category.id,
						date = data.timeInMillis,
					),
					isAppend = route.transactionId == null && data.timeInMillis == data.timeInMillisOriginal
				)
				state = state.copy(action = Action.SaveSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				state = state.copy(action = Action.SaveError)
			}
		}
		
	}
	
}