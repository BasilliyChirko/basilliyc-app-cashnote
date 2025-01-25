package basilliyc.cashnote.ui.account.balance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.AccountManager
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextInputState
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.asPriceWithCoins
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.launch

class AccountBalanceViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	private val accountManager: AccountManager by inject()
	
	val state = mutableStateOf(AccountBalanceState.Page())
	private var mState by state
	
	//Initialization of the state
	init {
		mState = mState.copy(content = AccountBalanceState.Content.Loading)
		val route = savedStateHandle.toRoute<AppNavigation.AccountBalance>()
		viewModelScope.launch {
			val account = (accountManager.getAccountById(route.id)
				?: throw IllegalStateException("Account with id ${route.id} is not present in database"))
			mState = mState.copy(
				content = AccountBalanceState.Content.Data(
					account = account,
					balanceDifference = TextInputState(""),
					balanceNew = TextInputState(account.balance.asPriceWithCoins()),
					comment = TextInputState(""),
				)
			)
		}
	}
	
	private fun updateStateContentData(call: AccountBalanceState.Content.Data.() -> AccountBalanceState.Content.Data) {
		val content = state.value.content
		if (content is AccountBalanceState.Content.Data) {
			state.value = state.value.copy(content = content.call())
		}
	}
	
	fun onBalanceDifferenceChanged(balanceDifferenceString: String) {
		val account = (mState.content as? AccountBalanceState.Content.Data)?.account ?: return
		
		val balanceDifferenceString = balanceDifferenceString.replace(",", ".")
		
		val balanceDifferenceValue = balanceDifferenceString.toDoubleOrNull() ?: 0.0
		
		val balanceNewValue = account.balance + balanceDifferenceValue
		
		updateStateContentData {
			copy(
				balanceDifference = TextInputState(balanceDifferenceString),
				balanceNew = TextInputState(balanceNewValue.asPriceWithCoins()),
				isBalanceReduce = (balanceDifferenceValue < 0).takeIf { balanceNewValue != account.balance },
				//TODO balanceDifferenceError
			)
		}
	}
	
	fun onBalanceNewChanged(balanceNewString: String) {
		val account = (mState.content as? AccountBalanceState.Content.Data)?.account ?: return
		
		val balanceNewString = balanceNewString.replace(",", ".")
		
		val balanceNewValue = balanceNewString.toDoubleOrNull() ?: 0.0
		
		val balanceDifference = balanceNewValue - account.balance
		
		updateStateContentData {
			copy(
				balanceDifference = TextInputState(balanceDifference.asPriceWithCoins()),
				balanceNew = TextInputState(balanceNewString),
				isBalanceReduce = (balanceDifference < 0).takeIf { balanceNewValue != account.balance },
			)
		}
		
	}
	
	fun onCommentChanged(comment: String) {
		updateStateContentData {
			copy(
				comment = TextInputState(comment),
			)
		}
	}
	
	fun onSaveClicked()  {
		logcat.debug("onSaveClicked")
		val data = (mState.content as? AccountBalanceState.Content.Data) ?: return
		val account = data.account
		
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
				accountManager.createTransaction(
					accountId = account.id,
					value = transactionValue,
					comment = data.comment.value,
				)
				mState = mState.copy(action = AccountBalanceState.Action.SaveSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				mState = mState.copy(action = AccountBalanceState.Action.SaveError)
			}
			logcat.debug("onSaveClicked", "handled")
		}
		
	}
	
	fun onCancelClicked() = handleEvent(skipIfBusy = true, postDelay = true) {
		mState = mState.copy(action = AccountBalanceState.Action.Cancel)
	}
	
	
}