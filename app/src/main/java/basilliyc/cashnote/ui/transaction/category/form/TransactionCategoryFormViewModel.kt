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
	
	var state by mutableStateOf(TransactionCategoryFormState())
		private set
	private var stateContentData
		get() = state.content as? TransactionCategoryFormState.Content.Data
		set(value) {
			if (value != null) state = state.copy(content = value)
		}
	
	private val route: AppNavigation.TransactionCategoryForm = savedStateHandle.toRoute()
	
	init {
		state = state.copy(content = TransactionCategoryFormState.Content.Loading)
		if (route.categoryId != null) {
			viewModelScope.launch {
				val category = (financialManager.getTransactionCategoryById(route.categoryId)
					?: throw IllegalStateException("TransactionCategory with id ${route.categoryId} is not present in database"))
				state = state.copy(content = TransactionCategoryFormState.Content.Data(category))
			}
		} else {
			val newCategory = FinancialTransactionCategory(
				name = "",
				icon = null,
			)
			state = state.copy(content = TransactionCategoryFormState.Content.Data(newCategory))
		}
	}
	
	private fun updateStateContentData(call: TransactionCategoryFormState.Content.Data.() -> TransactionCategoryFormState.Content.Data) {
		val content = state.content
		if (content is TransactionCategoryFormState.Content.Data) {
			state = state.copy(content = content.call())
		}
	}
	
	fun onActionConsumed() {
		state = state.copy(action = null)
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
	
	fun onSaveClicked() {
		val data = stateContentData ?: return
		
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
		
		scheduleEvent(skipIfBusy = true, postDelay = true) {
			try {
				financialManager.saveTransactionCategory(category)
				state = state.copy(action = TransactionCategoryFormState.Action.SaveSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				state = state.copy(action = TransactionCategoryFormState.Action.SaveError)
			}
		}
	}
	
	fun onDeleteClicked() {
		if (route.categoryId == null) return
		
		scheduleEvent(skipIfBusy = true, postDelay = true) {
			try {
				financialManager.deleteTransactionCategory(route.categoryId)
				state = state.copy(action = TransactionCategoryFormState.Action.DeleteSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				state = state.copy(action = TransactionCategoryFormState.Action.DeleteError)
			}
		}
	}
	
}