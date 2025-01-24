package basilliyc.cashnote.backend.manager

import androidx.room.withTransaction
import basilliyc.cashnote.backend.database.AppDatabase
import basilliyc.cashnote.backend.database.AppDatabaseAccountRepository
import basilliyc.cashnote.backend.database.AppDatabaseTransactionRepository
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.Transaction
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountManager {
	
	private val appDatabase: AppDatabase by inject()
	private val accountRepository: AppDatabaseAccountRepository by inject()
	private val transactionRepository: AppDatabaseTransactionRepository by inject()
	
	fun getAccountsListAsFlow() = accountRepository.getListAsFlow()
	
	suspend fun getAccountById(id: Long) = accountRepository.getById(id)
	
	suspend fun saveAccount(account: Account) = accountRepository.insert(account)
	
	suspend fun createTransaction(
		accountId: Long,
		value: Double,
		comment: String,
	) {
		
		if (value == 0.0) return
		
		val account = accountRepository.getById(accountId)
			?: throw IllegalStateException("Can`t create transaction. Account with id $accountId is not present in database")
		
		val transaction = Transaction(
			value = value,
			date = System.currentTimeMillis(),
			comment = comment,
			accountId = account.id,
		)
		
		appDatabase.withTransaction {
			transactionRepository.insert(transaction)
			accountRepository.insert(account.copy(balance = account.balance + value))
		}
	}
	
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