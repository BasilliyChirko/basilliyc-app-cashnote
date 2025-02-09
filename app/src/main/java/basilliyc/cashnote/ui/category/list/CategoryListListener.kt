package basilliyc.cashnote.ui.category.list

interface CategoryListListener {
	fun onResultHandled()
	fun onCategoryClicked(categoryId: Long)
	fun onCategoryAddClicked()
	fun onDragStarted()
	fun onDragCompleted(from: Int, to: Int)
	fun onDragReverted()
	fun onDragMoved(from: Int, to: Int)
}