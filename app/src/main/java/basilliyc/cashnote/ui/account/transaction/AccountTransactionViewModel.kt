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
	
	val state = mutableStateOf(AccountTransactionState.Page())
	private var mState by state
	
	//Initialization of the state
	init {
		mState = mState.copy(content = AccountTransactionState.Content.Loading)
		val route = savedStateHandle.toRoute<AppNavigation.AccountTransaction>()
		viewModelScope.launch {
			
			val account = (financialManager.getAccountById(route.accountId)
				?: throw IllegalStateException("Account with id ${route.accountId} is not present in database"))
			
			val availableCategories = financialManager.getAvailableTransactionCategories()
			
			mState = mState.copy(
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
				updateStateContentData {
					copy(availableCategories = categories)
				}
			}
		}
	}
	
	private fun updateStateContentData(call: AccountTransactionState.Content.Data.() -> AccountTransactionState.Content.Data) {
		val content = state.value.content
		if (content is AccountTransactionState.Content.Data) {
			state.value = state.value.copy(content = content.call())
		}
	}
	
	fun onBalanceDifferenceChanged(balanceDifferenceString: String) {
		val account = (mState.content as? AccountTransactionState.Content.Data)?.financialAccount ?: return
		
		val balanceDifferenceString = balanceDifferenceString.replace(",", ".")
		
		val balanceDifferenceValue = balanceDifferenceString.toDoubleOrNull() ?: 0.0
		
		val balanceNewValue = account.balance + balanceDifferenceValue
		
		updateStateContentData {
			copy(
				balanceDifference = TextFieldState(balanceDifferenceString),
				balanceNew = TextFieldState(balanceNewValue.asPriceWithCoins()),
				isBalanceReduce = (balanceDifferenceValue < 0).takeIf { balanceNewValue != account.balance },
			)
		}
	}
	
	fun onBalanceNewChanged(balanceNewString: String) {
		val account = (mState.content as? AccountTransactionState.Content.Data)?.financialAccount ?: return
		
		val balanceNewString = balanceNewString.replace(",", ".")
		
		val balanceNewValue = balanceNewString.toDoubleOrNull() ?: 0.0
		
		val balanceDifference = balanceNewValue - account.balance
		
		updateStateContentData {
			copy(
				balanceDifference = TextFieldState(balanceDifference.asPriceWithCoins()),
				balanceNew = TextFieldState(balanceNewString),
				isBalanceReduce = (balanceDifference < 0).takeIf { balanceNewValue != account.balance },
			)
		}
		
	}
	
	fun onCommentChanged(comment: String) {
		updateStateContentData {
			copy(
				comment = TextFieldState(comment),
			)
		}
	}
	
	fun onCategoryChanged(categoryId: Long?) {
		updateStateContentData {
			copy(
				selectedCategoryId = categoryId,
			)
		}
	}
	
	fun onSaveClicked()  {
		val data = (mState.content as? AccountTransactionState.Content.Data) ?: return
		val account = data.financialAccount
		
		val transactionValue = data.balanceDifference.value.toDoubleOrNull()
		
		if (transactionValue == null) {
			updateStateContentData {
				copy(
					balanceDifference = balanceDifference.copy(
						error = TextFieldError.IncorrectValue
					)
				)
			}
			return
		}
		
		handleEvent(
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
				mState = mState.copy(action = AccountTransactionState.Action.SaveSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				mState = mState.copy(action = AccountTransactionState.Action.SaveError)
			}
		}
		
	}
	
	fun onCancelClicked() = handleEvent(skipIfBusy = true, postDelay = true) {
		mState = mState.copy(action = AccountTransactionState.Action.Cancel)
	}
	
	
}