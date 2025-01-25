package basilliyc.cashnote.ui.transaction.category.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.data.FinancialTransactionCategoryIcon
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.launch
import kotlin.getValue

class TransactionCategoryFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	private val financialManager: FinancialManager by inject()
	
	val state = mutableStateOf(TransactionCategoryFormState.Page())
	private var mState by state
	
	private val route: AppNavigation.TransactionCategoryForm = savedStateHandle.toRoute()
	
	init {
		mState = mState.copy(content = TransactionCategoryFormState.Content.Loading)
		if (route.categoryId != null) {
			viewModelScope.launch {
				val category = (financialManager.getTransactionCategoryById(route.categoryId)
					?: throw IllegalStateException("TransactionCategory with id ${route.categoryId} is not present in database"))
				mState = mState.copy(content = TransactionCategoryFormState.Content.Data(category))
			}
		} else {
			val newCategory = FinancialTransactionCategory(
				name = "",
				icon = null,
			)
			mState = mState.copy(content = TransactionCategoryFormState.Content.Data(newCategory))
		}
	}
	
	private fun updateStateContentData(call: TransactionCategoryFormState.Content.Data.() -> TransactionCategoryFormState.Content.Data) {
		val content = state.value.content
		if (content is TransactionCategoryFormState.Content.Data) {
			state.value = state.value.copy(content = content.call())
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
	
	fun onIconChanged(icon: FinancialTransactionCategoryIcon?) {
		updateStateContentData {
			copy(icon = icon.takeIf { icon != this.icon })
		}
	}
	
	fun onCancelClicked() {
		mState = mState.copy(action = TransactionCategoryFormState.Action.Cancel)
	}
	
	fun onSaveClicked() {
		val data = mState.content as? TransactionCategoryFormState.Content.Data ?: return
		
		val nameString = data.name.value
		val nameTextError = getNameTextError(nameString)
		if (nameTextError != null) {
			updateStateContentData {
				copy(name = name.copy(error = nameTextError))
			}
			return
		}
		
		val category = FinancialTransactionCategory(
			id = route.categoryId ?: 0L,
			name = nameString,
			icon = data.icon,
		)
		
		handleEvent(skipIfBusy = true, postDelay = true) {
			try {
				financialManager.saveTransactionCategory(category)
				mState = mState.copy(action = TransactionCategoryFormState.Action.SaveSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				mState = mState.copy(action = TransactionCategoryFormState.Action.SaveError)
			}
		}
	}
	
	fun onDeleteClicked() {
		if (route.categoryId == null) return
		
		handleEvent(skipIfBusy = true, postDelay = true) {
			try {
				financialManager.deleteTransactionCategory(route.categoryId)
				mState = mState.copy(action = TransactionCategoryFormState.Action.DeleteSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				mState = mState.copy(action = TransactionCategoryFormState.Action.DeleteError)
			}
		}
	}
	
}