package basilliyc.cashnote.ui.account.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.AccountManager
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.letIf
import kotlinx.coroutines.launch

class AccountFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	private val accountManager: AccountManager by inject()
	
	val state = mutableStateOf(AccountFormState.Page())
	private var mState by state
	private var accountId: Long = 0
	
	//Initialization of the state
	init {
		mState = mState.copy(content = AccountFormState.Content.Loading)
		val route = savedStateHandle.toRoute<AppNavigation.AccountForm>()
		accountId = route.id ?: 0L
		if (route.id != null) {
			viewModelScope.launch {
				val account = (accountManager.getAccountById(route.id)
					?: throw IllegalStateException("Account with id ${route.id} is not present in database"))
				mState = mState.copy(content = AccountFormState.Content.Data(account))
			}
		} else {
			val account = Account(
				name = "",
				currency = AccountCurrency.UAH,
				color = null,
				balance = 0.0
			)
			mState = mState.copy(content = AccountFormState.Content.Data(account))
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
			copy(name = name, nameError = getNameTextError(name))
		}
	}
	
	private fun getNameTextError(name: String): TextFieldError? {
		if (name.isBlank()) return TextFieldError.ShouldNotBeEmpty
		return null
	}
	
	fun onBalanceChanged(balance: String) {
		val balance = balanceStringCorrection(balance)
		updateStateContentData {
			copy(balance = balance, balanceError = getBalanceTextError(balance))
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
	
	fun onSaveClicked(navController: NavController) {
		val content = state.value.content as? AccountFormState.Content.Data ?: return
		
		val name = content.name
		val nameTextError = getNameTextError(name)
		if (nameTextError != null) {
			updateStateContentData {
				copy(nameError = nameTextError)
			}
			return
		}
		
		val balance = balanceStringCorrection(content.balance)
		val balanceTextError = getBalanceTextError(balance)
		if (balanceTextError != null) {
			updateStateContentData {
				copy(balanceError = balanceTextError)
			}
			return
		}
		
		val account = Account(
			id = accountId,
			name = name,
			currency = content.currency,
			color = content.color,
			balance = balance.toDouble()
		)
		
		viewModelScope.launch {
			try {
				accountManager.saveAccount(account)
				navController.popBackStack()
			} catch (t: Throwable) {
				logcat.error(t)
			}
		}
		
	}
	
	
}
