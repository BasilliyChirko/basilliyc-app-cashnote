package basilliyc.cashnote.ui.account.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategoryToFinancialAccountParams
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.utils.letIf
import basilliyc.cashnote.utils.updateIf
import kotlinx.coroutines.launch

class AccountFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), AccountFormListener {
	
	
	val state = AccountFormStateHolder()
	
	private val route: AppNavigation.AccountForm = savedStateHandle.toRoute()
	private var editedAccount: FinancialAccount? = null
	
	//Initialization of the state
	init {
		state.page = AccountFormStateHolder.Page.Loading
		
		val accountIdOnNavigation = preferences.accountIdOnNavigation.value
		
		viewModelScope.launch {
			val account = if (route.accountId != null) {
				financialManager.requireAccountById(route.accountId).also {
					editedAccount = it
				}
			} else {
				FinancialAccount(
					name = "",
					currency = FinancialCurrency.UAH,
					color = null,
					balance = 0.0,
					position = 0,
				)
			}
			
			val categories = financialManager.getCategoryList()

			val useCategoryIds = route.accountId
				?.let { financialManager.getCategoryToAccountParamsListByAccountId(it) }
				?.filter { it.visible }
				?.map { it.categoryId }
				?: categories.map { it.id }
			
			state.page = AccountFormStateHolder.Page.Data(
				account = account,
				isShowOnNavigation = account.id == accountIdOnNavigation,
				categories = categories.map { category ->
					AccountFormStateHolder.CategoryWithUsing(
						category = category,
						using = category.id in useCategoryIds
					)
				}
			)
			
		}
	}
	
	override fun onResultHandled() {
		state.result = null
	}
	
	override fun onCurrencyChanged(currency: FinancialCurrency) {
		state.pageData = state.pageData?.copy(
			currency = currency
		)
	}
	
	override fun onNameChanged(name: String) {
		state.pageDataName = TextFieldState(
			value = name,
			error = getNameTextError(name)
		)
	}
	
	private fun getNameTextError(name: String): TextFieldError? {
		if (name.isBlank()) return TextFieldError.ShouldNotBeEmpty
		return null
	}
	
	override fun onBalanceChanged(balance: String) {
		val balance = balanceStringCorrection(balance)
		state.pageDataBalance = TextFieldState(
			value = balance,
			error = getBalanceTextError(balance)
		)
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
		state.pageData = state.pageData?.copy(color = color)
	}
	
	override fun onSaveClicked() {
		val data = state.pageData ?: return
		
		val nameString = data.name.value.trim()
		val nameTextError = getNameTextError(nameString)
		if (nameTextError != null) {
			state.pageDataName = data.name.copy(error = nameTextError)
			return
		}
		
		val balanceString = balanceStringCorrection(data.balance.value)
		val balanceTextError = getBalanceTextError(balanceString)
		if (balanceTextError != null) {
			state.pageDataBalance = data.balance.copy(error = balanceTextError)
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
				val params = data.categories.map {
					FinancialCategoryToFinancialAccountParams(
						accountId = account.id,
						categoryId = it.category.id,
						visible = it.using
					)
				}
				
				val accountId = financialManager.saveAccount(account, params)
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
				
				state.result = AccountFormStateHolder.Result.SaveSuccess(
					isNew = data.isNew,
					isNeedRebuildApp = isNeedRebuildApp
				)
			} catch (t: Throwable) {
				logcat.error(t)
				state.result = AccountFormStateHolder.Result.SaveError
			}
		}
		
	}
	
	override fun onShowOnNavigationChanged(show: Boolean) {
		state.pageData = state.pageData?.copy(isShowOnNavigation = show)
	}
	
	override fun onCategoryClicked(categoryId: Long) {
		val data = state.pageData ?: return
		state.pageData = data.copy(
			categories = data.categories.updateIf({ it.category.id == categoryId }) {
				it.copy(using = !it.using)
			},
		)
	}
	
	
}
