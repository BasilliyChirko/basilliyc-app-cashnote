@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Kayaking
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation.*
import basilliyc.cashnote.R
import basilliyc.cashnote.data.color
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.account.list.AccountListStateHolder.AccountBalance
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.components.BalanceText
import basilliyc.cashnote.ui.components.CardBalance
import basilliyc.cashnote.ui.components.CardBalanceLeadingIcon
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.symbol
import basilliyc.cashnote.ui.theme.backgroundCardGradient
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.DraggableVerticalGrid
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.applyIf
import basilliyc.cashnote.utils.toPriceColor
import basilliyc.cashnote.utils.toPriceString

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountList() {
	val viewModel = viewModel<AccountListViewModel>()
	Result(result = viewModel.state.result, listener = viewModel)
	Page(page = viewModel.state.page, listener = viewModel)
}

@Composable
private fun Result(
	result: AccountListStateHolder.Result?,
	listener: AccountListListener,
) {
	handleResult(result, listener) {
		when (it) {
			is AccountListStateHolder.Result.NavigateAccountDetails -> {
				navigateForward(
					AccountDetails(
						accountId = it.accountId,
						isFromNavigation = false
					)
				)
			}
			
			is AccountListStateHolder.Result.NavigateAccountForm -> {
				navigateForward(AccountForm(accountId = null))
			}
			
			is AccountListStateHolder.Result.NavigateAccountTransaction -> {
				navigateForward(
					TransactionForm(
						accountId = it.accountId,
						categoryId = it.categoryId,
						transactionId = null,
					)
				)
			}
			
		}
	}
}


@Preview(showBackground = true)
@Composable
private fun AccountListPreview() = DefaultPreview {
	PageData(
		page = AccountListStateHolder.Page.Data(
			accounts = PreviewValues.accounts.map {
				AccountBalance(
					account = it,
					primaryValue = it.balance,
				)
			},
			accountsDragged = null,
		),
		listener = object : AccountListListener {
			override fun onResultHandled() {}
			override fun onAddNewAccountClicked() {}
			override fun onAccountClicked(id: Long) {}
			override fun onAccountLongClicked(id: Long) {}
			override fun onDragStarted() {}
			override fun onDragCompleted(from: Int, to: Int) {}
			override fun onDragReverted() {}
			override fun onDragMoved(from: Int, to: Int) {}
		}
	)
}

@Composable
private fun Page(
	page: AccountListStateHolder.Page,
	listener: AccountListListener,
) {
	when (page) {
		is AccountListStateHolder.Page.Data -> PageData(page, listener)
		AccountListStateHolder.Page.Loading -> PageLoading()
	}
}

@Composable
private fun PageData(
	page: AccountListStateHolder.Page.Data,
	listener: AccountListListener,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				title = stringResource(R.string.main_nav_account_list),
				actions = {
					IconButton(
						onClick = listener::onAddNewAccountClicked,
						imageVector = Icons.Filled.Add,
						contentDescription = stringResource(R.string.add_new_account)
					)
				},
				navigationIcon = {}
			)
		}
	) {
		if (page.accounts.isEmpty()) {
			PageDataEmpty(
				onClickAddNewAccount = listener::onAddNewAccountClicked
			)
			return@ScaffoldBox
		}
		
		
		val accounts = page.accountsDragged ?: page.accounts
		DraggableVerticalGrid(
			modifier = Modifier.fillMaxSize(),
			columns = if (page.isSingleLine) GridCells.Fixed(1) else GridCells.Adaptive(128.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(8.dp),
			onDragStarted = listener::onDragStarted,
			onDragCompleted = listener::onDragCompleted,
			onDragReverted = listener::onDragReverted,
			onDragMoved = listener::onDragMoved,
			onLongClick = { listener.onAccountLongClicked(accounts[it].account.id) }
		) {
			items(
				count = accounts.size,
				key = { accounts[it].account.id },
				itemContent = { index, isDragged ->
					val accountBalance = accounts[index]
					val account = accountBalance.account
					if (page.isSingleLine) {
						AccountCardItem(
							modifier = Modifier
								.applyIf({ isDragged }) {
									this.shadow(
										elevation = 4.dp,
										shape = MaterialTheme.shapes.small
									)
								},
							onClick = { listener.onAccountClicked(account.id) },
							balance = accountBalance,
						)
					} else {
						CardBalance(
							modifier = Modifier
								.applyIf({ isDragged }) {
									this.shadow(
										elevation = 4.dp,
										shape = MaterialTheme.shapes.small
									)
								},
							onClick = { listener.onAccountClicked(account.id) },
							title = account.name,
							primaryValue = account.balance,
							secondaryValue = accountBalance.primaryValue,
							leadingIcon = CardBalanceLeadingIcon(account.currency),
							color = account.color,
							isWide = page.isSingleLine,
						)
					}
				}
			)
		}
		
	}
}

@Composable
private fun AccountCardItem(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	balance: AccountBalance,
) {
	val account = balance.account
	OutlinedCard(
		modifier = modifier,
		onClick = onClick,
		shape = MaterialTheme.shapes.small,
		border = account.color?.color?.let { BorderStroke(1.dp, it) }
			?: CardDefaults.outlinedCardBorder(),
	) {
		Box(
			modifier = Modifier.backgroundCardGradient(account.color)
		) {
			Row(
				modifier = Modifier
					.padding(8.dp)
					.defaultMinSize(minHeight = 48.dp),
				verticalAlignment = Alignment.CenterVertically,
				content = {
					
					Text(
						modifier = Modifier.weight(1F),
						text = account.name,
						style = MaterialTheme.typography.titleLarge,
						maxLines = 2,
					)
					
					Column(
						horizontalAlignment = Alignment.End,
						verticalArrangement = Arrangement.Center,
					) {
						
						BalanceText(
							text = account.balance.toPriceString(showPlus = false),
							modifier = Modifier,
							style = MaterialTheme.typography.titleLarge,
							coinsTextStyle = MaterialTheme.typography.titleMedium,
						)
						
						if (balance.primaryValue != null) {
							BalanceText(
								modifier = Modifier,
								text = balance.primaryValue.toPriceString(showPlus = true),
								style = MaterialTheme.typography.bodyMedium,
								color = balance.primaryValue.toPriceColor(),
								coinsTextStyle = MaterialTheme.typography.bodyMedium
							)
						}
						
					}
					
					Text(
						modifier = Modifier.padding(start = 8.dp),
						text = account.currency.symbol,
						style = MaterialTheme.typography.headlineSmall,
						fontFamily = FontFamily.Monospace,
					)
				}
			)
		}
	}
}

@Composable
private fun PageDataEmpty(
	modifier: Modifier = Modifier,
	onClickAddNewAccount: () -> Unit,
) {
	Column(
		modifier = modifier
			.padding(16.dp)
			.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Image(
			modifier = Modifier.size(128.dp),
			imageVector = Icons.Filled.Kayaking,
			contentDescription = stringResource(R.string.account_list_empty_title),
			colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = stringResource(R.string.account_list_empty_title),
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.headlineMedium,
		)
		Spacer(modifier = Modifier.height(16.dp))
		OutlinedButton(
			onClick = onClickAddNewAccount,
			icon = Icons.Filled.Add,
			text = stringResource(R.string.account_list_empty_submit)
		)
	}
}


