package basilliyc.cashnote.ui.test

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.ui.transaction.history.TransactionHistoryViewModel

@Composable
fun ListTest() {
	
	val viewModel = viewModel<TransactionHistoryViewModel>()

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