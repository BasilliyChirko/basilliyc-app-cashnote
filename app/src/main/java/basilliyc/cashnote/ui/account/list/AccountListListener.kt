package basilliyc.cashnote.ui.account.list

import basilliyc.cashnote.ui.base.BaseListener

interface AccountListListener : BaseListener {
	fun onAddNewAccountClicked()
	fun onAccountClicked(id: Long)
	fun onAccountLongClicked(id: Long)
	fun onDragStarted()
	fun onDragCompleted(from: Int, to: Int)
	fun onDragReverted()
	fun onDragMoved(from: Int, to: Int)
}