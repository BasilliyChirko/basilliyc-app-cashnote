package basilliyc.cashnote.ui.category.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation.*
import basilliyc.cashnote.R
import basilliyc.cashnote.data.color
import basilliyc.cashnote.ui.base.rememberInteractionHelper
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.utils.DraggableVerticalGrid
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.applyIf

@Composable
fun CategoryList() {
	val viewModel = viewModel<CategoryListViewModel>()
	Page(page = viewModel.state.page, listener = viewModel)
	Result(result = viewModel.state.result, listener = viewModel)
}

@Composable
private fun Result(
	result: CategoryListStateHolder.Result?,
	listener: CategoryListListener,
) {
	rememberInteractionHelper().handle(result) {
		listener.onResultHandled()
		when (it) {
			
			null -> Unit
			
			is CategoryListStateHolder.Result.NavigateCategoryForm -> {
				navigateForward(CategoryForm(it.categoryId))
			}
			
		}
	}
}

@Composable
private fun Page(
	page: CategoryListStateHolder.Page,
	listener: CategoryListListener,
) {
	when (page) {
		is CategoryListStateHolder.Page.Data -> PageData(page, listener)
		CategoryListStateHolder.Page.Loading -> PageLoading()
	}
}

@Composable
private fun PageData(
	page: CategoryListStateHolder.Page.Data,
	listener: CategoryListListener,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				title = { Text(text = stringResource(R.string.transaction_categories_title)) },
				actions = {
					IconButton(
						onClick = listener::onCategoryAddClicked,
						imageVector = Icons.Filled.Add,
						contentDescription = stringResource(R.string.transaction_categories_action_add)
					)
				}
			)
		}
	) {
		val categories = page.categoriesDragged ?: page.categories
		
		DraggableVerticalGrid(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			onDragStarted = { listener.onDragStarted() },
			onDragMoved = listener::onDragMoved,
			onDragCompleted = listener::onDragCompleted,
			onDragReverted = listener::onDragReverted,
			columns = GridCells.Adaptive(140.dp),
		) {
			items(
				count = categories.size,
				key = { categories[it].category.id },
				animateItem = true,
				itemContent = { index, isDragged ->
					CategoryItem(
						modifier = Modifier
							.applyIf({ isDragged }) {
								this.shadow(
									elevation = 4.dp,
									shape = MaterialTheme.shapes.small
								)
							},
						categoryWithCount = categories[index],
						onClick = listener::onCategoryClicked,
					)
				}
			)
		}
	}
}

@Composable
private fun LazyGridItemScope.CategoryItem(
	modifier: Modifier = Modifier,
	categoryWithCount: CategoryListStateHolder.CategoryWithCount,
	onClick: (Long) -> Unit,
) {
	val category = categoryWithCount.category
	OutlinedCard(
		modifier = modifier
			.fillMaxWidth(),
		onClick = { onClick(category.id) },
		colors = CardDefaults.outlinedCardColors(
			containerColor = category.color.color,
		),
		shape = MaterialTheme.shapes.small,
		border = category.color?.color?.let { BorderStroke(1.dp, it) }
			?: CardDefaults.outlinedCardBorder(),
	) {
		Row(
			modifier = Modifier.padding(vertical = 8.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			category.icon?.imageVector?.let { icon ->
				Icon(
					imageVector = icon,
					contentDescription = category.name,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
			Column(
				modifier = Modifier.padding(8.dp),
			) {
				Text(
					text = category.name,
					style = MaterialTheme.typography.titleMedium
				)
				Text(
					text = pluralStringResource(
						R.plurals.category_list_item_used_in_accounts_count,
						categoryWithCount.visibleInAccountsCount,
						categoryWithCount.visibleInAccountsCount,
					),
				)
			}
			
		}
	}
}