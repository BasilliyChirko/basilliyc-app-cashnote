package basilliyc.cashnote.ui.account.transaction.category.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCategoryIcon
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.activity.AppNavigation
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.launch
import kotlin.getValue

class CategoryFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
	
	var state by mutableStateOf(CategoryFormState())
		private set
	private var stateContentData
		get() = state.content as? CategoryFormState.Content.Data
		set(value) {
			if (value != null) state = state.copy(content = value)
		}
	
	private val route: AppNavigation.CategoryForm = savedStateHandle.toRoute()
	
	init {
		state = state.copy(content = CategoryFormState.Content.Loading)
		if (route.categoryId != null) {
			viewModelScope.launch {
				val category = (financialManager.getCategoryById(route.categoryId)
					?: throw IllegalStateException("TransactionCategory with id ${route.categoryId} is not present in database"))
				state = state.copy(content = CategoryFormState.Content.Data(category))
			}
		} else {
			val newCategory = FinancialCategory(
				name = "",
				icon = null,
			)
			state = state.copy(content = CategoryFormState.Content.Data(newCategory))
		}
	}
	
	private fun updateStateContentData(call: CategoryFormState.Content.Data.() -> CategoryFormState.Content.Data) {
		val content = state.content
		if (content is CategoryFormState.Content.Data) {
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
	
	fun onIconChanged(icon: FinancialCategoryIcon?) {
		updateStateContentData {
			copy(icon = icon.takeIf { icon != this.icon })
		}
	}
	
	fun onSaveClicked() {
		val data = stateContentData ?: return
		
		val nameString = data.name.value.trim()
		val nameTextError = getNameTextError(nameString)
		if (nameTextError != null) {
			updateStateContentData {
				copy(name = name.copy(error = nameTextError))
			}
			return
		}
		
		val category = FinancialCategory(
			id = route.categoryId ?: 0L,
			name = nameString,
			icon = data.icon,
		)
		
		schedule(skipIfBusy = true, postDelay = true) {
			try {
				financialManager.saveCategory(category)
				state = state.copy(action = CategoryFormState.Action.SaveSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				state = state.copy(action = CategoryFormState.Action.SaveError)
			}
		}
	}
	
	fun onDeleteClicked() {
		if (route.categoryId == null) return
		
		schedule(skipIfBusy = true, postDelay = true) {
			try {
				financialManager.deleteCategory(route.categoryId)
				state = state.copy(action = CategoryFormState.Action.DeleteSuccess)
			} catch (t: Throwable) {
				logcat.error(t)
				state = state.copy(action = CategoryFormState.Action.DeleteError)
			}
		}
	}
	
}