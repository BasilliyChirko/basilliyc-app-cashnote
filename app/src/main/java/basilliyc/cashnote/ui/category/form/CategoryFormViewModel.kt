package basilliyc.cashnote.ui.category.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCategoryToFinancialAccountParams
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialIcon
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.ui.category.form.CategoryFormStateHolder.Page
import basilliyc.cashnote.ui.category.form.CategoryFormStateHolder.Result
import basilliyc.cashnote.ui.components.TextFieldError
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.utils.flowZip
import basilliyc.cashnote.utils.updateIf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryFormViewModel(
	savedStateHandle: SavedStateHandle,
) : BaseViewModel(), CategoryFormListener {
	
	private val route: AppNavigation.CategoryForm = savedStateHandle.toRoute()
	
	val state = CategoryFormStateHolder()
	
	init {
		viewModelScope.launch {
			state.page = Page.Loading
			
			val category = if (route.categoryId != null) {
				financialManager.requireCategoryById(route.categoryId)
			} else {
				FinancialCategory(
					name = "",
					icon = null,
					color = null,
				)
			}
			
			state.page = Page.Data(category, emptyList())
			
			listenForUpdates()
		}
	}
	
	private fun listenForUpdates() {
		viewModelScope.launch {
			if (route.categoryId == null) return@launch
			financialManager.getCategoryByIdAsFlow(route.categoryId).collect {
				if (it == null) {
					state.result = Result.NavigateBack
				}
			}
		}
		
		viewModelScope.launch {
			flowZip(
				financialManager.getAccountsListAsFlow(),
				financialManager.getCategoryToAccountParamsListAsFlow(),
			) { accounts, categoryToAccountParamsList ->
				accounts.map {
					CategoryFormStateHolder.AccountWithUsing(
						account = it,
						using = categoryToAccountParamsList.find { params ->
							params.accountId == it.id && params.categoryId == route.categoryId
						}?.visible != false
					)
				}
			}.collectLatest {
				state.pageData = state.pageData?.copy(accounts = it)
			}
		}
	}
	
	override fun onResultConsumed() {
		state.result = null
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
	
	override fun onIconChanged(icon: FinancialIcon?) {
		state.pageData = state.pageData?.copy(icon = icon)
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
		
		schedule(skipIfBusy = true, postDelay = true) {
			
			val category = FinancialCategory(
				id = route.categoryId ?: 0L,
				name = nameString,
				icon = data.icon,
				color = data.color,
			)
			
			val params = data.accounts.map {
				FinancialCategoryToFinancialAccountParams(
					accountId = it.account.id,
					categoryId = category.id,
					visible = it.using
				)
			}
			
			try {
				financialManager.saveCategory(category, params)
				state.result = Result.SaveSuccess(data.isNew)
			} catch (t: Throwable) {
				logcat.error(t)
				state.result = Result.SaveError
			}
		}
	}
	
	override fun onDeleteClicked() {
		val categoryId: Long = route.categoryId ?: return
		
		schedule(skipIfBusy = true, postDelay = true) {
			try {
				
				val extendedResult = financialManager.checkExtendedDeletionForCategory(categoryId)
				
				if (extendedResult.transactionCount == 0) {
					//No transactions. Just delete category
					financialManager.deleteCategoryExtended(
						categoryId,
						FinancialManager.DeleteCategoryExtendedStrategy.DeleteTransactions(
							affectAccounts = false
						)
					)
					state.result = Result.DeleteSuccess
				} else {
					state.result = Result.NavigateCategoryExtendedDeletion(categoryId)
				}
				
			} catch (t: Throwable) {
				logcat.error(t)
				state.result = Result.DeleteError
			}
		}
	}
	
	override fun onAccountClicked(accountId: Long) {
		val data = state.pageData ?: return
		state.pageData = data.copy(
			accounts = data.accounts.updateIf({ it.account.id == accountId }) {
				it.copy(using = !it.using)
			}
		)
	}
	
}