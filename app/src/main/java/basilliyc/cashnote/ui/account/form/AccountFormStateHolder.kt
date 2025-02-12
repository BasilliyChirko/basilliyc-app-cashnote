package basilliyc.cashnote.ui.account.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.ui.components.TextFieldState


class AccountFormStateHolder(
	page: Page = Page.Loading,
	result: Result? = null,
) {
	
	var page by mutableStateOf(page)
	
	var pageData
		get() = page as? Page.Data
		set(value) =
			if (value != null) page = value
			else Unit
	
	var pageDataName
		get() = pageData?.name
		set(value) =
			if (value != null) pageData = pageData?.copy(name = value)
			else Unit
	
	var pageDataBalance
		get() = pageData?.balance
		set(value) =
			if (value != null) pageData = pageData?.copy(balance = value)
			else Unit
	
	var result by mutableStateOf(result)
	
	sealed interface Page {
		data object Loading : Page
		data class Data(
			val isNew: Boolean,
			val currency: FinancialCurrency,
			val name: TextFieldState,
			val balance: TextFieldState,
			val color: FinancialColor?,
			val isShowOnNavigation: Boolean,
			val categories: List<CategoryWithUsing>,
		) : Page {
			constructor(
				account: FinancialAccount,
				isShowOnNavigation: Boolean,
				categories: List<CategoryWithUsing>,
			) : this(
				isNew = account.id == 0L,
				currency = account.currency,
				name = TextFieldState(value = account.name),
				balance = TextFieldState(value = account.balance.toString()),
				color = account.color,
				isShowOnNavigation = isShowOnNavigation,
				categories = categories,
			)
		}
	}
	
	data class CategoryWithUsing(
		val category: FinancialCategory,
		val using: Boolean,
	)
	
	sealed interface Result {
		data class SaveSuccess(val isNew: Boolean, val isNeedRebuildApp: Boolean) : Result
		data object Cancel : Result
		data object SaveError : Result
	}
	
}