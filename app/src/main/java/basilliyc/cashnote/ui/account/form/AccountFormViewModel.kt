package basilliyc.cashnote.ui.account.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.letIf
import kotlinx.coroutines.launch

class AccountFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	private val financialManager: FinancialManager by inject()
	
	val state = mutableStateOf(AccountFormState.Page())
	private var mState by state
	
	private val route: AppNavigation.AccountForm = savedStateHandle.toRoute()
	
	//Initialization of the state
	init {
		mState = mState.copy(content = AccountFormState.Content.Loading)
		if (route.accountId != null) {
			viewModelScope.launch {
				val account = (financialManager.getAccountById(route.accountId)
					?: throw IllegalStateException("Account with id ${route.accountId} is not present in database"))
				mState = mState.copy(content = AccountFormState.Content.Data(account))
			}
		} else {
			val financialAccount = FinancialAccount(
				name = "",
				currency = AccountCurrency.UAH,
				color = null,
				balance = 0.0
			)
			mState = mState.copy(content = AccountFormState.Content.Data(financialAccount))
		}
	}
	
	private fun updateStateContentData(call: AccountFormState.Content.Data.() -> AccountFormState.Content.Data) {
		val content = state.value.content
		if (content is AccountFormState.Content.Data) {
			state.value = state.value.copy(content = content.call())
		}
	}
	
	fun onCurrencyChanged(currency: AccountCurrency) {
		updateStateContentData {
			copy(currency = currency)
		}
	}
	
	fun onNameChanged(name: String) {
		updateStateContentData {
			copy(
				name = TextFieldState(
					value = name,
					error = getNameTextError(name)
				)
			)
		}
	}
	
	private fun getNameTextError(name: String): TextFieldError? {
		if (name.isBlank()) return TextFieldError.ShouldNotBeEmpty
		return null
	}
	
	fun onBalanceChanged(balance: String) {
		val balance = balanceStringCorrection(balance)
		updateStateContentData {
			copy(
				balance = TextFieldState(
					value = balance,
					error = getBalanceTextError(balance)
				)
			)
		}
	}
	
	private fun balanceStringCorrection(balance: String): String {
		return balance.replace(",", ".")
			.letIf(
				condition = { it.split(".").getOrElse(1, defaultValue = { "" }).length > 2 },
				block = {
					logcat.debug(it)
					buildString {
						val split = it.split(".")
						logcat.debug(split)
						append(split[0])
						append(".")
						append(split[1].take(2))
					}
				}
			)
	}
	
	private fun getBalanceTextError(balance: String): TextFieldError? {
		if (balance.isBlank()) return TextFieldError.ShouldNotBeEmpty
		if (balance.toDoubleOrNull() == null) return TextFieldError.IncorrectValue
		return null
	}
	
	fun onColorChanged(color: AccountColor) {
		updateStateContentData {
			copy(color = color.takeIf { color != this.color })
		}
	}
	
	fun onSaveClicked() {
		val content = state.value.content as? AccountFormState.Content.Data ?: return
		
		val nameString = content.name.value
		val nameTextError = getNameTextError(nameString)
		if (nameTextError != null) {
			updateStateContentData {
				copy(name = name.copy(error = nameTextError))
			}
			return
		}
		
		val balanceString = balanceStringCorrection(content.balance.value)
		val balanceTextError = getBalanceTextError(balanceString)
		if (balanceTextError != null) {
			updateStateContentData {
				copy(balance = balance.copy(error = balanceTextError))
			}
			return
		}
		
		val financialAccount = FinancialAccount(
			id = route.accountId ?: 0L,
			name = nameString,
			currency = content.currency,
			color = content.color,
			balance = balanceString.toDouble()
		)
		
		handleEvent(skipIfBusy = true, postDelay = true) {
			try {
				financialManager.saveAccount(financialAccount)
				mState = mState.copy(action = AccountFormState.Action.SaveSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				mState = mState.copy(action = AccountFormState.Action.SaveError)
			}
		}
		
	}
	
	
}
