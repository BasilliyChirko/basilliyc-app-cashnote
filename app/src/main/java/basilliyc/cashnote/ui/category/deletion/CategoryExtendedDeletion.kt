package basilliyc.cashnote.ui.category.deletion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.base.rememberInteractionHelper
import basilliyc.cashnote.ui.category.deletion.CategoryExtendedDeletionStateHolder.DeletionStrategy
import basilliyc.cashnote.ui.components.BackButton
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.menu.MenuRowPopup
import basilliyc.cashnote.ui.components.menu.MenuRowSwitch
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.showToast

@Composable
fun CategoryExtendedDeletion() {
	val viewModel = viewModel<CategoryExtendedDeletionViewModel>()
	Page(page = viewModel.state.page, listener = viewModel)
	Result(result = viewModel.state.result, listener = viewModel)
}

@Composable
private fun Result(
	result: CategoryExtendedDeletionStateHolder.Result?,
	listener: CategoryExtendedDeletionListener,
) {
	rememberInteractionHelper().handle(result) {
		listener.onResultConsumed()
		when (result) {
			null -> Unit
			
			CategoryExtendedDeletionStateHolder.Result.DeletionError -> {
				context.showToast(R.string.category_extended_deletion_error)
			}
			
			CategoryExtendedDeletionStateHolder.Result.DeletionSuccess -> {
				context.showToast(R.string.category_extended_deletion_success)
				navController.popBackStack()
			}
			
			CategoryExtendedDeletionStateHolder.Result.DeletionTargetCategoryRequired -> {
				context.showToast(R.string.category_extended_deletion_target_category_required)
			}
		}
	}
}

@Composable
private fun Page(
	page: CategoryExtendedDeletionStateHolder.Page,
	listener: CategoryExtendedDeletionListener,
) {
	when (page) {
		is CategoryExtendedDeletionStateHolder.Page.Data -> PageData(
			page = page,
			listener = listener
		)
		
		CategoryExtendedDeletionStateHolder.Page.Loading -> BoxLoading()
	}
}

@Composable
@Preview(showBackground = true)
private fun PageDataPreview() = DefaultPreview {
	PageData(
		page = CategoryExtendedDeletionStateHolder.Page.Data(
			category = PreviewValues.categoryHome,
			categoryExtendedDeletionResult = FinancialManager.CategoryExtendedDeletionResult(
				transactionCount = 5
			),
			deletionStrategy = DeletionStrategy.DeleteTransactions,
			strategyDeleteTransactionsAffectAccounts = false,
			strategyChangeTransactionsCategoryTargetCategory = null,
			strategyChangeTransactionsCategoryAvailableCategories = PreviewValues.categories,
		),
		listener = object : CategoryExtendedDeletionListener {
			override fun onResultConsumed() = Unit
			override fun onDeletionStrategyChanged(strategy: DeletionStrategy) =
				Unit
			
			override fun onStrategyDeleteTransactionsAffectAccountsChanged(affect: Boolean) = Unit
			override fun onStrategyChangeTransactionsCategoryTargetCategoryChanged(category: FinancialCategory?) =
				Unit
			
			override fun onDeleteClicked() = Unit
		}
	)
}

@Composable
private fun PageData(
	page: CategoryExtendedDeletionStateHolder.Page.Data,
	listener: CategoryExtendedDeletionListener,
) {
	Surface(
		shape = MaterialTheme.shapes.medium,
	) {
		
		Column {
			SimpleActionBar(
				title = stringResource(R.string.category_extended_deletion_title),
				navigationIcon = { BackButton(Icons.Filled.Close) }
			)
			
			Text(
				modifier = Modifier.padding(horizontal = 16.dp),
				text = stringResource(
					R.string.category_extended_deletion_message,
					page.categoryExtendedDeletionResult.transactionCount,
					page.category.name,
				),
			)
			
			MenuRowPopup(
				title = stringResource(R.string.category_extended_deletion_strategy_label),
				value = page.deletionStrategy.text(),
			) {
				DeletionStrategy.entries.forEach { strategy ->
					PopupMenuItem(
						text = strategy.text(),
						onClick = { listener.onDeletionStrategyChanged(strategy) }
					)
				}
			}
			
			when (page.deletionStrategy) {
				DeletionStrategy.ChangeTransactionsCategory -> {
					MenuRowPopup(
						title = stringResource(R.string.category_extended_deletion_strategy_change_transactions_category_target),
						value = page.strategyChangeTransactionsCategoryTargetCategory?.name
							?: stringResource(R.string.category_extended_deletion_strategy_change_transactions_category_target_none),
					) {
						page.strategyChangeTransactionsCategoryAvailableCategories.forEach {
							PopupMenuItem(
								text = it.name,
								onClick = {
									listener.onStrategyChangeTransactionsCategoryTargetCategoryChanged(
										it
									)
								}
							)
						}
					}
				}
				
				DeletionStrategy.DeleteTransactions -> {
					MenuRowSwitch(
						title = stringResource(R.string.category_extended_deletion_strategy_delete_transactions_affect_accounts),
						checked = page.strategyDeleteTransactionsAffectAccounts,
						onCheckedChange = listener::onStrategyDeleteTransactionsAffectAccountsChanged
					)
				}
			}
			
			OutlinedButton(
				onClick = listener::onDeleteClicked,
				text = stringResource(R.string.category_extended_deletion_action_delete),
				modifier = Modifier
					.padding(16.dp)
					.fillMaxWidth(),
			)
		}
	}
	
}

@Composable
private fun DeletionStrategy.text(): String {
	return stringResource(
		when (this) {
			DeletionStrategy.ChangeTransactionsCategory -> R.string.category_extended_deletion_strategy_change_transactions_category
			DeletionStrategy.DeleteTransactions -> R.string.category_extended_deletion_strategy_delete_transactions
		}
	)
}
