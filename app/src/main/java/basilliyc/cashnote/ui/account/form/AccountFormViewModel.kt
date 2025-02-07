package basilliyc.cashnote.ui.account.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.AppPreferences
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.ui.activity.AppNavigation
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.letIf
import kotlinx.coroutines.launch

class AccountFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), AccountFormListener {
	
	
	var state by mutableStateOf(AccountFormState())
		private set
	private var statePageData
		get() = state.page as? AccountFormState.Page.Data
		set(value) {
			if (value != null) state = state.copy(page = value)
		}
	
	private fun updateStateContentData(call: AccountFormState.Page.Data.() -> AccountFormState.Page.Data) {
		val content = statePageData
		if (content is AccountFormState.Page.Data) {
			statePageData = statePageData?.call()
		}
	}
	
	private val route: AppNavigation.AccountForm = savedStateHandle.toRoute()
	private var editedAccount: FinancialAccount? = null
	
	//Initialization of the state
	init {
		state = state.copy(page = AccountFormState.Page.Loading)
		
		val accountIdOnNavigation = preferences.accountIdOnNavigation.value
		
		if (route.accountId != null) {
			viewModelScope.launch {
				val account = (financialManager.getAccountById(route.accountId)
					?: throw IllegalStateException("Account with id ${route.accountId} is not present in database"))
				editedAccount = account
				state = state.copy(
					page = AccountFormState.Page.Data(
						account = account,
						isShowOnNavigation = account.id == accountIdOnNavigation
					),
				)
			}
		} else {
			val account = FinancialAccount(
				name = "",
				currency = AccountCurrency.UAH,
				color = null,
				balance = 0.0,
				position = 0,
			)
			state = state.copy(
				page = AccountFormState.Page.Data(
					account = account,
					isShowOnNavigation = account.id == accountIdOnNavigation
				),
			)
		}
	}
	
	override fun onActionConsumed() {
		state = state.copy(action = null)
	}
	
	override fun onCurrencyChanged(currency: AccountCurrency) {
		updateStateContentData {
			copy(currency = currency)
		}
	}
	
	override fun onNameChanged(name: String) {
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
	
	override fun onBalanceChanged(balance: String) {
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
	
	override fun onColorChanged(color: FinancialColor?) {
		updateStateContentData {
			copy(color = color.takeIf { color != this.color })
		}
	}
	
	override fun onSaveClicked() {
		val data = statePageData ?: return
		
		val nameString = data.name.value.trim()
		val nameTextError = getNameTextError(nameString)
		if (nameTextError != null) {
			updateStateContentData {
				copy(name = name.copy(error = nameTextError))
			}
			return
		}
		
		val balanceString = balanceStringCorrection(data.balance.value)
		val balanceTextError = getBalanceTextError(balanceString)
		if (balanceTextError != null) {
			updateStateContentData {
				copy(balance = balance.copy(error = balanceTextError))
			}
			return
		}
		
		val account = FinancialAccount(
			id = route.accountId ?: 0L,
			name = nameString,
			currency = data.currency,
			color = data.color,
			balance = balanceString.toDouble(),
			position = editedAccount?.position ?: 0,
		)
		
		schedule(skipIfBusy = true, postDelay = true) {
			try {
				val accountId = financialManager.saveAccount(account)
				var accountIdOnNavigation by preferences.accountIdOnNavigation
				var isNeedRebuildApp = false
				when {
					!data.isShowOnNavigation && accountIdOnNavigation == accountId -> {
						accountIdOnNavigation = null
						isNeedRebuildApp = true
					}
					
					data.isShowOnNavigation && accountIdOnNavigation != accountId -> {
						accountIdOnNavigation = accountId
						isNeedRebuildApp = true
					}
				}
				
				state = state.copy(
					action = AccountFormState.Action.SaveSuccess(
						isNew = data.isNew,
						isNeedRebuildApp = isNeedRebuildApp
					)
				)
			} catch (t: Throwable) {
				logcat.error(t)
				state = state.copy(action = AccountFormState.Action.SaveError)
			}
		}
		
	}
	
	override fun onShowOnNavigationChanged(show: Boolean) {
		statePageData = statePageData?.copy(
			isShowOnNavigation = show
		)
	}
	
	
}
