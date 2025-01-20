@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.account_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Kayaking
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.OutlinedButton


//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun AccountList() {
	val viewModel = viewModel<AccountListViewModel>()
	val logcat = viewModel.logcat
	AccountListContent(
		state = viewModel.state,
		onClickAddNewAccount = {
			logcat.info("onClickAddNewAccount")
		},
		onClickAccount = {
			logcat.info("onClickAccount(id=$it)")
		},
	)
}

@Preview(showBackground = true)
@Composable
private fun AccountListPreview() = DefaultPreview {
	AccountListContent(
		state = AccountListState.Page(
			content = AccountListState.Content.Data(
				listOf(
					Account(id = 1, name = "Account 1"),
					Account(id = 2, name = "Account 2"),
					Account(id = 3, name = "Account 3"),
				)
			)
//			content = AccountListState.Content.Loading
//			content = AccountListState.Content.DataEmpty
		)
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------


@Composable
private fun AccountListContent(
	state: AccountListState.Page,
	onClickAddNewAccount: () -> Unit = {},
	onClickAccount: (id: Long) -> Unit = {},
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			if (state.content is AccountListState.Content.Data) {
				ActionBar(onClickAddNewAccount)
			}
		},
		content = { innerPadding ->
			val modifier = Modifier.padding(innerPadding)
			
			when (val content = state.content) {
				is AccountListState.Content.Data -> AccountListContentData(
					modifier = modifier,
					content = content,
					onClickAccount = onClickAccount,
				)
				
				is AccountListState.Content.Loading -> AccountListContentLoading(
					modifier = modifier
				)
				
				AccountListState.Content.DataEmpty -> AccountListContentDataEmpty(
					modifier = modifier,
					onClickAddNewAccount = onClickAddNewAccount
				)
			}
			
		}
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT.LOADING
//--------------------------------------------------------------------------------------------------

@Composable
private fun AccountListContentLoading(
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		CircularProgressIndicator()
	}
}

//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA_EMPTY
//--------------------------------------------------------------------------------------------------

@Composable
private fun AccountListContentDataEmpty(
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

//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA
//--------------------------------------------------------------------------------------------------

@Composable
private fun AccountListContentData(
	modifier: Modifier = Modifier,
	content: AccountListState.Content.Data,
	onClickAccount: (id: Long) -> Unit,
) {
	LazyVerticalGrid(
		modifier = modifier,
		columns = GridCells.Adaptive(128.dp),
	) {
		items(
			count = content.accounts.size,
			key = { content.accounts[it].id },
		) {
			AccountListContentListItem(
				account = content.accounts[it],
				onClickAccount = onClickAccount,
			)
		}
	}
}

@Composable
private fun AccountListContentListItem(
	account: Account,
	onClickAccount: (id: Long) -> Unit,
) {
	Card(
		modifier = Modifier.padding(8.dp),
		onClick = { onClickAccount(account.id) },
	) {
		Column {
			Text(text = account.name, modifier = Modifier.padding(8.dp))
			Row {
				Text(text = "$", modifier = Modifier.padding(horizontal = 8.dp))
				Spacer(modifier = Modifier.weight(1F))
				Text(text = "1000.00", modifier = Modifier.padding(horizontal = 8.dp))
			}
			Row {
				Spacer(modifier = Modifier.weight(1F))
				Text(text = "-150.40", modifier = Modifier.padding(8.dp))
			}
		}
	}
}


//--------------------------------------------------------------------------------------------------
//  ACTION BAR
//--------------------------------------------------------------------------------------------------

@Composable
private fun ActionBar(
	onClickAddNewAccount: () -> Unit,
) = TopAppBar(
	title = { Text(text = stringResource(R.string.main_nav_account_list)) },
//	title = { },
	actions = {
		IconButton(
			onClick = onClickAddNewAccount
		) {
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = stringResource(R.string.add_new_account)
			)
		}
	}
)

