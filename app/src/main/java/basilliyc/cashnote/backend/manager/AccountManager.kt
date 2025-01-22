package basilliyc.cashnote.backend.manager

import basilliyc.cashnote.backend.database.AppDatabaseAccountRepository
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountManager {
	
	private val accountRepository: AppDatabaseAccountRepository by inject()
	
	fun getAccountsListAsFlow() = accountRepository.getAccountsListAsFlow()
	
	suspend fun getAccountById(id: Long) = accountRepository.getAccountById(id)
	
	suspend fun saveAccount(account: Account) = accountRepository.insertAccount(account)
	
	fun test() = CoroutineScope(Dispatchers.Default).launch {
//		accountRepository.insertAccount(
//			Account(
//				name = "Test",
//				currency = AccountCurrency.UAH,
//				color = AccountColor.Orange,
//				balance = 100.50,
//			)
//		)
	}
	
}