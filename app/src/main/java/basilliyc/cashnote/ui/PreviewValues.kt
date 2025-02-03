package basilliyc.cashnote.ui

import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCategoryIcon

object PreviewValues {
	
	val accountFilled by lazy {
		FinancialAccount(
			id = 0L,
			name = "Test Account",
			currency = AccountCurrency.USD,
			color = AccountColor.Green,
			balance = 3456.78,
			position = 0
		)
	}
	
	val categoryHome by lazy {
		categories.find { it.icon == FinancialCategoryIcon.Home }!!
	}
	
	val categories by lazy {
		FinancialCategoryIcon.entries.mapIndexed { index, icon ->
			FinancialCategory(
				id = index.toLong(),
				name = icon.name,
				position = index,
				icon = icon
			)
		}
	}
	
}