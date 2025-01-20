package basilliyc.cashnote.backend.manager

import basilliyc.cashnote.backend.database.AppDatabaseAccountRepository
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.utils.inject

class AccountManager {
	
	private val accountRepository: AppDatabaseAccountRepository by inject()
	
	fun getAccountsListAsFlow() = accountRepository.getAccountsListAsFlow()
	
	
}