package basilliyc.cashnote.ui

import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.data.FinancialTransactionCategoryIcon

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
		categories[0]
	}
	
	val categories by lazy {
		FinancialTransactionCategoryIcon.entries.mapIndexed { index, icon ->
			FinancialTransactionCategory(
				id = index.toLong(),
				name = icon.name,
				position = index,
				icon = icon
			)
		}
	}
	
}