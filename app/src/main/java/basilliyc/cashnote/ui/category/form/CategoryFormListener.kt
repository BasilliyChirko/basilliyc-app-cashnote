package basilliyc.cashnote.ui.category.form

import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialIcon

interface CategoryFormListener {
	fun onResultConsumed()
	fun onNameChanged(name: String)
	fun onIconChanged(icon: FinancialIcon?)
	fun onColorChanged(color: FinancialColor?)
	fun onSaveClicked()
	fun onDeleteClicked()
}