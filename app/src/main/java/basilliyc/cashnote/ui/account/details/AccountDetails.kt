package basilliyc.cashnote.ui.account.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.labelText
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.base.rememberInteractionHelper
import basilliyc.cashnote.ui.components.BackButton
import basilliyc.cashnote.ui.components.BalanceText
import basilliyc.cashnote.ui.components.CardBalance
import basilliyc.cashnote.ui.components.CardBalanceLeadingIcon
import basilliyc.cashnote.ui.components.DeleteConfirmationDialog
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.PopupState
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.ui.theme.backgroundPageGradient
import basilliyc.cashnote.ui.theme.colorGrey99
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.applyIf
import basilliyc.cashnote.utils.toPriceString

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountDetails() {
	val viewModel = viewModel<AccountDetailsViewModel>()
	Page(state = viewModel.state, listener = viewModel)
	Dialog(dialog = viewModel.state.dialog, listener = viewModel)
	Result(result = viewModel.state.result, listener = viewModel)
}

//--------------------------------------------------------------------------------------------------
//  ACTION
//--------------------------------------------------------------------------------------------------

@Composable
private fun Result(
	result: AccountDetailsState.Result?,
	listener: AccountDetailsListener,
) {
	rememberInteractionHelper().handle(result) {
		listener.onResultConsumed()
		when (it) {
			null -> Unit
			
			is AccountDetailsState.Result.NavigateTransactionForm -> navigateForward(
				AppNavigation.TransactionForm(
					accountId = it.accountId,
					categoryId = it.categoryId,
					transactionId = null,
				)
			)
			
			AccountDetailsState.Result.NavigateCategoryList -> navigateForward(
				AppNavigation.CategoryList
			)
			
			is AccountDetailsState.Result.NavigateAccountForm -> navigateForward(
				AppNavigation.AccountForm(accountId = it.accountId)
			)
			
			is AccountDetailsState.Result.NavigateAccountHistory -> navigateForward(
				AppNavigation.TransactionHistory(
					accountId = it.accountId,
					isFromNavigation = false,
				)
			)
			
			AccountDetailsState.Result.AccountDeletionSuccess -> {
				showToast(R.string.account_details_toast_account_deletion_success)
				navigateBack()
			}
			
			AccountDetailsState.Result.AccountDeletionError -> {
				showToast(R.string.account_details_toast_account_deletion_error)
			}
		}
	}
}


//--------------------------------------------------------------------------------------------------
//  PAGE
//--------------------------------------------------------------------------------------------------

@Composable
@Preview(showBackground = true)
private fun PageDataPreview() = DefaultPreview {
	PageData(
		page = AccountDetailsState.Page.Data(
			account = PreviewValues.accountTestUSD,
			statisticParams = PreviewValues.statisticParams,
			balancePrimaryPositive = 100.0,
			balancePrimaryNegative = -100.0,
			balanceSecondaryPositive = 200.0,
			balanceSecondaryNegative = -200.0,
			categories = PreviewValues.categories.map {
				AccountDetailsState.CategoryWithBalance(
					category = it,
					primaryValue = 500.0,
					secondaryValue = -40.0,
				)
			},
		),
		listener = object : AccountDetailsListener {
			override fun onResultConsumed() = Unit
			override fun onCategoryClicked(id: Long) = Unit
			override fun onAccountCategoriesClicked() = Unit
			override fun onAccountEditClicked() = Unit
			override fun onAccountHistoryClicked() = Unit
			override fun onAccountDeleteClicked() = Unit
			override fun onDeleteAccountConfirmed() = Unit
			override fun onDeleteAccountCanceled() = Unit
		}
	)
}

@Composable
private fun Page(
	state: AccountDetailsState,
	listener: AccountDetailsListener,
) {
	when (val content = state.page) {
		is AccountDetailsState.Page.Data -> PageData(
			page = content,
			listener = listener,
			showBackButton = state.showBackButton,
		)
		
		AccountDetailsState.Page.Loading -> PageLoading(
			showBackButton = state.showBackButton,
		)
	}
}

@Composable
private fun PageData(
	page: AccountDetailsState.Page.Data,
	listener: AccountDetailsListener,
	showBackButton: Boolean = true,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				title = page.account.name,
				actions = {
					PopupMenu(
						anchor = {
							IconButton(
								onClick = { expand() },
								imageVector = Icons.Filled.MoreVert,
								contentDescription = stringResource(R.string.account_details_menu)
							)
						},
						items = {
							PageDataOptionsMenu(
								page = page,
								listener = listener,
							)
						},
					)
				},
				containerColor = page.account.color.color,
				navigationIcon = {
					if (showBackButton) {
						BackButton()
					}
				}
			)
		},
		content = {
			Column(
				modifier = Modifier
					.verticalScroll(rememberScrollState())
					.applyIf({ page.account.color != null }) {
						backgroundPageGradient(page.account.color)
					},
			) {
				PageDataBalance(page)
				PageDataCategories(
					page = page,
					listener = listener,
				)
			}
		}
	)
}

@Composable
private fun PopupState.PageDataOptionsMenu(
	page: AccountDetailsState.Page.Data,
	listener: AccountDetailsListener,
) {
	PopupMenuItem(
		onClick = listener::onAccountCategoriesClicked,
		text = stringResource(R.string.account_details_menu_categories),
		leadingIcon = Icons.Filled.Category,
	)
	PopupMenuItem(
		text = stringResource(R.string.account_transaction_action_edit_accout),
		onClick = listener::onAccountEditClicked,
		leadingIcon = Icons.Filled.Edit,
	)
	PopupMenuItem(
		text = stringResource(R.string.account_transaction_action_history),
		onClick = listener::onAccountHistoryClicked,
		leadingIcon = Icons.Filled.History,
	)
	PopupMenuItem(
		text = stringResource(R.string.account_transaction_action_delete_accout),
		onClick = listener::onAccountDeleteClicked,
		leadingIcon = Icons.Filled.DeleteForever,
	)
}

@Composable
private fun ColumnScope.PageDataBalance(
	page: AccountDetailsState.Page.Data,
) {
	Spacer(modifier = Modifier.height(8.dp))
	OutlinedCard(
		modifier = Modifier
			.padding(horizontal = 8.dp),
		shape = MaterialTheme.shapes.small,
	) {
		BalanceRow(
			modifier = Modifier.padding(vertical = 16.dp),
			title = stringResource(R.string.account_balance),
			value = page.account.balance,
			currency = page.account.currency,
		)
		
		if (page.statisticParams.showAccountStatistic) {
			HorizontalDivider()
			
			Column(
				modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
			) {
				
				if (page.statisticParams.showSecondaryValueForAccount) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
					) {
						Spacer(modifier = Modifier.weight(1F))
						Text(
							text = page.statisticParams.primaryValueCalculation.labelText(),
							modifier = Modifier.weight(1.5F),
							maxLines = 1,
							style = MaterialTheme.typography.bodyMedium,
							textAlign = TextAlign.End,
							color = colorGrey99,
						)
						Text(
							text = page.statisticParams.secondaryValueCalculation.labelText(),
							modifier = Modifier.weight(1.5F),
							maxLines = 1,
							style = MaterialTheme.typography.bodyMedium,
							textAlign = TextAlign.End,
							color = colorGrey99,
						)
					}
				}
				
				
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					BalanceStatsTitle(title = stringResource(R.string.account_balance_receive))
					BalanceStatsValue(value = page.balancePrimaryPositive, showPlus = false)
					if (page.statisticParams.showSecondaryValueForAccount && page.balanceSecondaryPositive != null) {
						BalanceStatsValue(value = page.balanceSecondaryPositive, showPlus = false)
					}
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					BalanceStatsTitle(title = stringResource(R.string.account_balance_spend))
					BalanceStatsValue(value = page.balancePrimaryNegative, showPlus = false)
					if (page.statisticParams.showSecondaryValueForAccount && page.balanceSecondaryNegative != null) {
						BalanceStatsValue(value = page.balanceSecondaryNegative, showPlus = false)
					}
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					val valuePrimary = page.balancePrimaryPositive + page.balancePrimaryNegative
					val valueSecondary =
						page.balanceSecondaryPositive?.plus(page.balanceSecondaryNegative ?: 0.0)
					BalanceStatsTitle(title = stringResource(R.string.account_balance_profit))
					BalanceStatsValue(value = valuePrimary, showPlus = true)
					if (page.statisticParams.showSecondaryValueForAccount && valueSecondary != null) {
						BalanceStatsValue(value = valueSecondary, showPlus = true)
					}
				}
				
			}
			
		}
	}
}


@Composable
private fun BalanceRow(
	modifier: Modifier = Modifier,
	title: String,
	value: Double,
	currency: FinancialCurrency,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = title,
			modifier = Modifier.weight(1f),
			maxLines = 1,
			style = MaterialTheme.typography.titleLarge,
		)
		BalanceText(
			text = buildString {
				append(currency.symbol)
				append(" ")
				append(value.toPriceString(false))
			},
			style = MaterialTheme.typography.titleLarge,
			coinsTextStyle = MaterialTheme.typography.titleMedium,
		)
	}
}

@Composable
private fun RowScope.BalanceStatsTitle(
	title: String,
) {
	Text(
		text = title,
		modifier = Modifier.weight(1F),
		maxLines = 1,
		style = MaterialTheme.typography.bodyLarge,
		textAlign = TextAlign.Start,
	)
}

@Composable
private fun RowScope.BalanceStatsValue(
	value: Double,
	showPlus: Boolean = false,
) {
	Box(
		modifier = Modifier.weight(1.5F),
		contentAlignment = Alignment.CenterEnd,
	) {
		BalanceText(
			text = value.toPriceString(showPlus),
			style = MaterialTheme.typography.titleMedium,
			coinsTextStyle = MaterialTheme.typography.bodyLarge,
		)
	}
}

@Composable
private fun ColumnScope.PageDataCategories(
	page: AccountDetailsState.Page.Data,
	listener: AccountDetailsListener,
) {
	val categories = page.categories
	
	if (categories.isEmpty()) {
		PageDataCategoriesEmpty(onEmptyCategoriesSubmitted = listener::onAccountCategoriesClicked)
		return
	}
	
	VerticalGrid(
		modifier = Modifier.padding(
			horizontal = 8.dp,
			vertical = 8.dp,
		),
		columns = VerticalGridCells.Adaptive(140.dp),
		itemsCount = categories.size,
		verticalSpace = 8.dp,
		horizontalSpace = 8.dp,
	) { index ->
		val category = categories[index]
		CardBalance(
			modifier = Modifier,
			onClick = { listener.onCategoryClicked(category.category.id) },
			title = category.category.name,
			primaryValue = category.primaryValue,
			secondaryValue = category.secondaryValue?.takeIf { page.statisticParams.showSecondaryValueForCategory },
			leadingIcon = CardBalanceLeadingIcon(category.category.icon?.imageVector),
			color = category.category.color,
		)
	}
}

@Composable
private fun ColumnScope.PageDataCategoriesEmpty(
	onEmptyCategoriesSubmitted: () -> Unit,
) {
	Column(
		modifier = Modifier
			.padding(horizontal = 16.dp, vertical = 32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = stringResource(R.string.account_details_categories_empty),
			textAlign = TextAlign.Center,
		)
		OutlinedButton(
			onClick = onEmptyCategoriesSubmitted,
			modifier = Modifier.padding(top = 16.dp),
			text = stringResource(R.string.account_details_categories_empty_submit),
			icon = Icons.Filled.Category,
		)
	}
}

//--------------------------------------------------------------------------------------------------
//  DIALOG
//--------------------------------------------------------------------------------------------------

@Composable
private fun Dialog(
	dialog: AccountDetailsState.Dialog?,
	listener: AccountDetailsListener,
) {
	
	when (dialog) {
		null -> Unit
		AccountDetailsState.Dialog.AccountDeleteConfirmation -> DeleteConfirmationDialog(
			title = stringResource(R.string.account_details_delete_confirmation_title),
			text = stringResource(R.string.account_details_delete_confirmation_text),
			onConfirm = listener::onDeleteAccountConfirmed,
			onCancel = listener::onDeleteAccountCanceled,
		)
	}
	
}