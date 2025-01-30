package basilliyc.cashnote.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingSource
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.ui.account.history.AccountHistoryViewModel
import basilliyc.cashnote.utils.inject

@Composable
fun ListTest() {
	
	val viewModel = viewModel<AccountHistoryViewModel>()

//	val financialManager by remember { inject<FinancialManager>() }
	
//	financialManager.test()
	
//	val pagingSource = financialManager.getTransactionListPagingSource(accountId = 1L)
//
//	val loadResult = pagingSource.load(
//		params = PagingSource.LoadParams.Refresh<Int>(
//			key = null,
//			loadSize = 20,
//			placeholdersEnabled = false,
//		)
//	)
//
//	loadResult
//
//	pagingSource.load()
	
	
}