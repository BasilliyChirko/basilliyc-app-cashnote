package basilliyc.cashnote.ui.transaction.category.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.R
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.data.FinancialTransactionCategoryIcon
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.main.AppNavigation
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.DraggableLazyColumn
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.launch

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun TransactionCategoryList() {
	val financialManager by remember { inject<FinancialManager>() }
	
	val transactionCategories by financialManager.getAvailableTransactionCategoriesAsFlow()
		.collectAsState(emptyList())
	
	var transactionCategoriesDragged by remember {
		mutableStateOf<List<FinancialTransactionCategory>?>(null)
	}
	
	val coroutineScope = rememberCoroutineScope()
	
	val navController = LocalNavController.current
	
	Content(
		transactionCategories = transactionCategoriesDragged ?: transactionCategories,
		onCategoryClicked = {
			navController.navigate(AppNavigation.TransactionCategoryForm(it))
		},
		onCategoryAddClicked = {
			navController.navigate(AppNavigation.TransactionCategoryForm(null))
		},
		onDragStarted = {
			transactionCategoriesDragged = transactionCategories
		},
		onDragCompleted = { from, to ->
			
			transactionCategoriesDragged =
				transactionCategories.let { ArrayList(it) }.reordered(from, to)
			
			coroutineScope.launch {
				financialManager.changeTransactionCategoryPosition(from, to)
				transactionCategoriesDragged = null
			}
		},
		onDragReverted = {
			transactionCategoriesDragged = null
		},
		onDragMoved = { from, to ->
			transactionCategoriesDragged =
				transactionCategoriesDragged?.let { ArrayList(it) }?.reordered(from, to)
		}
	)
}

@Composable
@Preview(showBackground = true)
private fun TransactionCategoryListPreview() = DefaultPreview {
	val availableCategories = listOf(
		FinancialTransactionCategory(
			id = 1,
			name = "Home",
			icon = FinancialTransactionCategoryIcon.Home,
		),
		FinancialTransactionCategory(
			id = 2,
			name = "Person",
			icon = FinancialTransactionCategoryIcon.Person,
		),
		FinancialTransactionCategory(
			id = 3,
			name = "Other",
			icon = null,
		),
	)
	Content(
		transactionCategories = availableCategories,
		onCategoryClicked = {},
		onCategoryAddClicked = {},
		onDragCompleted = { _, _ -> },
		onDragReverted = {},
		onDragMoved = { _, _ -> },
		onDragStarted = {},
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	transactionCategories: List<FinancialTransactionCategory>,
	onCategoryClicked: (Long) -> Unit,
	onCategoryAddClicked: () -> Unit,
	onDragStarted: () -> Unit,
	onDragCompleted: (from: Int, to: Int) -> Unit,
	onDragReverted: () -> Unit,
	onDragMoved: (from: Int, to: Int) -> Unit,
) {
	Scaffold(
		topBar = {
			SimpleActionBar(
				title = { Text(text = stringResource(R.string.transaction_categories_title)) },
				actions = {
					IconButton(
						onClick = onCategoryAddClicked,
						imageVector = Icons.Filled.Add,
						contentDescription = stringResource(R.string.transaction_categories_action_add)
					)
				}
			)
		},
		content = { innerPadding ->
			
			DraggableLazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.padding(horizontal = 16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
				onDragStarted = onDragStarted,
				onDragMoved = onDragMoved,
				onDragCompleted = onDragCompleted,
				onDragReverted = onDragReverted,
				isOverscrollEnabled = false,
			) {
				items(
					count = transactionCategories.size,
					key = { transactionCategories[it].id },
					animateItem = true,
					itemContent = { index ->
						val category = transactionCategories[index]
						CategoryItem(
							category = category,
							onClick = onCategoryClicked,
						)
					}
				)
			}
		},
	)
}


@Composable
private fun LazyItemScope.CategoryItem(
	category: FinancialTransactionCategory,
	onClick: (Long) -> Unit,
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.animateItem(),
		onClick = { onClick(category.id) },
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
		) {
			category.icon?.imageVector?.let { icon ->
				Icon(
					imageVector = icon,
					contentDescription = category.name,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
			Text(
				modifier = Modifier.padding(8.dp),
				text = category.name,
				maxLines = 1,
			)
		}
	}
}