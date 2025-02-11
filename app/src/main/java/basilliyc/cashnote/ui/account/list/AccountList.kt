@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Kayaking
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.account.list.AccountListStateHolder.AccountBalance
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.components.CardBalance
import basilliyc.cashnote.ui.components.CardBalanceLeadingIcon
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.DraggableVerticalGrid
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.applyIf


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
					AppNavigation.AccountDetails(
						accountId = it.id,
						isFromNavigation = false
					)
				)
			}
			
			is AccountListStateHolder.Result.NavigateAccountForm -> {
				navigateForward(AppNavigation.AccountForm(accountId = null))
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
			override fun onClickAddNewAccount() {}
			override fun onClickAccount(id: Long) {}
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
						onClick = listener::onClickAddNewAccount,
						imageVector = Icons.Filled.Add,
						contentDescription = stringResource(R.string.add_new_account)
					)
				}
			)
			
		}
	) {
		if (page.accounts.isEmpty()) {
			PageDataEmpty(
				onClickAddNewAccount = listener::onClickAddNewAccount
			)
			return@ScaffoldBox
		}
		
		val accounts = page.accountsDragged ?: page.accounts
		DraggableVerticalGrid(
			modifier = Modifier.fillMaxSize(),
			columns = GridCells.Adaptive(128.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(8.dp),
			onDragStarted = { listener.onDragStarted() },
			onDragCompleted = listener::onDragCompleted,
			onDragReverted = listener::onDragReverted,
			onDragMoved = listener::onDragMoved,
		) {
			items(
				count = accounts.size,
				key = { accounts[it].account.id },
				itemContent = { index, isDragged ->
					val accountBalance = accounts[index]
					val account = accountBalance.account
					CardBalance(
						modifier = Modifier
							.applyIf({ isDragged }) {
								this.shadow(
									elevation = 4.dp,
									shape = MaterialTheme.shapes.small
								)
							},
						onClick = { listener.onClickAccount(account.id) },
						title = account.name,
						primaryValue = account.balance,
						secondaryValue = accountBalance.primaryValue,
						leadingIcon = CardBalanceLeadingIcon(account.currency),
						color = account.color,
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
