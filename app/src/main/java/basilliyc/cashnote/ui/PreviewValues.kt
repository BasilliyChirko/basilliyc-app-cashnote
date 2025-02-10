package basilliyc.cashnote.ui

import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialIcon
import basilliyc.cashnote.data.FinancialStatisticParams

object PreviewValues {
	
	val accountTestUSD by lazy {
		FinancialAccount(
			id = 0L,
			name = "Test Account USD",
			currency = FinancialCurrency.USD,
			color = FinancialColor.Green,
			balance = 3456.78,
			position = 0
		)
	}
	
	val accountTestEUR by lazy {
		FinancialAccount(
			id = 0L,
			name = "Test Account EUR",
			currency = FinancialCurrency.EUR,
			color = FinancialColor.Blue,
			balance = 3456.78,
			position = 0
		)
	}
	
	val categoryHome by lazy {
		categories.find { it.icon == FinancialIcon.Home }!!
	}
	
	val categories by lazy {
		FinancialIcon.entries.mapIndexed { index, icon ->
			FinancialCategory(
				id = index.toLong(),
				name = icon.name,
				position = index,
				icon = icon,
				color = null,
			)
		}
	}
	
	val statisticParams by lazy {
		FinancialStatisticParams()
	}
	
}