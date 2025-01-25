package basilliyc.cashnote.ui.transaction.category.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.inject

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun TransactionCategoryList() {
	val financialManager by remember { inject<FinancialManager>() }
	
	val transactionCategories by financialManager.getAvailableTransactionCategoriesAsFlow()
		.collectAsState(emptyList())
	
	val navController = LocalNavController.current
	
	Content(
		transactionCategories = transactionCategories,
		onCategoryClicked = {
			navController.navigate(AppNavigation.TransactionCategoryForm(it))
		},
		onCategoryAddClicked = {
			navController.navigate(AppNavigation.TransactionCategoryForm(null))
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
			LazyColumn(
				modifier = Modifier
					.padding(innerPadding)
					.padding(horizontal = 16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				items(
					count = transactionCategories.size,
					key = { transactionCategories[it].id },
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
private fun CategoryItem(
	category: FinancialTransactionCategory,
	onClick: (Long) -> Unit,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
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