package basilliyc.cashnote.backend.manager

import androidx.room.withTransaction
import basilliyc.cashnote.backend.database.AppDatabase
import basilliyc.cashnote.backend.database.DatabaseAccountRepository
import basilliyc.cashnote.backend.database.DatabaseTransactionCategoryRepository
import basilliyc.cashnote.backend.database.DatabaseTransactionRepository
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.FinancialTransaction
import basilliyc.cashnote.data.FinancialTransactionCategory
import basilliyc.cashnote.utils.Logcat
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.reordered
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class FinancialManager {
	
	@Suppress("unused")
	private val logcat = Logcat(this)
	
	private val appDatabase: AppDatabase by inject()
	private val accountRepository: DatabaseAccountRepository by inject()
	private val transactionRepository: DatabaseTransactionRepository by inject()
	private val transactionCategoryRepository: DatabaseTransactionCategoryRepository by inject()
	
	//----------------------------------------------------------------------------------------------
	//  Account
	//----------------------------------------------------------------------------------------------
	
	fun getAccountsListAsFlow() = accountRepository.getListAsFlow()
	
	suspend fun getAccountById(id: Long) = accountRepository.getById(id)
	
	fun getAccountByIdAsFlow(id: Long) = accountRepository.getByIdAsFlow(id)
	
	suspend fun saveAccount(financialAccount: FinancialAccount) =
		accountRepository.save(financialAccount)
	
	suspend fun deleteAccount(accountId: Long) = accountRepository.delete(accountId)
	
	//----------------------------------------------------------------------------------------------
	//  Transaction
	//----------------------------------------------------------------------------------------------
	
	suspend fun createTransaction(
		accountId: Long,
		value: Double,
		comment: String,
		categoryId: Long?,
	) {
		
		if (value == 0.0) return
		
		val account = accountRepository.getById(accountId)
			?: throw IllegalStateException("Can`t create transaction. Account with id $accountId is not present in database")
		
		//Validate is category really exists
		val categoryId = categoryId?.let {
			transactionCategoryRepository.getById(it)?.id
		}
		
		val transaction = FinancialTransaction(
			id = 0,
			value = value,
			date = System.currentTimeMillis(),
			comment = comment,
			accountId = account.id,
			categoryId = categoryId,
		)
		
		appDatabase.withTransaction {
			transactionRepository.save(transaction)
			accountRepository.save(account.copy(balance = account.balance + value))
		}
	}
	
	//----------------------------------------------------------------------------------------------
	//  Transaction Category
	//----------------------------------------------------------------------------------------------
	
	suspend fun getAvailableTransactionCategories() = transactionCategoryRepository.getList()
	
	fun getAvailableTransactionCategoriesAsFlow() = transactionCategoryRepository.getListAsFlow()
	
	suspend fun getTransactionCategoryById(id: Long) = transactionCategoryRepository.getById(id)
	
	suspend fun saveTransactionCategory(category: FinancialTransactionCategory) {
		
		val category = category.let {
			if (category.id == 0L) {
				var maxPosition = transactionCategoryRepository.getMaxPosition()
				
				if (maxPosition == 0) {
					if (transactionCategoryRepository.getItemsCount() == 0) {
						maxPosition = -1
					}
				}
				
				it.copy(position = maxPosition + 1)
			} else it
		}
		
		transactionCategoryRepository.save(category)
	}
	
	suspend fun deleteTransactionCategory(categoryId: Long) =
		transactionCategoryRepository.delete(categoryId)
	
	suspend fun changeTransactionCategoryPosition(from: Int, to: Int) {
		val categories = transactionCategoryRepository.getList()
			.reordered(from, to)
			.mapIndexed { index, category ->
				category.copy(
					position = index
				)
			}
		transactionCategoryRepository.save(categories)
	}
	
	//----------------------------------------------------------------------------------------------
	//  Other
	//----------------------------------------------------------------------------------------------
	
	fun test() = CoroutineScope(Dispatchers.Default).launch {

//		val account = accountRepository.getList().firstOrNull() ?: let {
//			accountRepository.insert(
//				FinancialAccount(
//					id = 0L,
//					name = "Test",
//					currency = AccountCurrency.UAH,
//					color = null,
//					balance = 100.500,
//				)
//			)
//			accountRepository.getList().first()
//		}
//
//		accountRepository.getList().forEach {
//			logcat.debug(it)
//			transactionRepository.getList(it.id).forEach {
//				logcat.debug(it)
//			}
//		}
//
//		transactionRepository.insert(
//			FinancialTransaction(
//				id = 0,
//				value = 100.0,
//				date = System.currentTimeMillis(),
//				comment = "Test",
//				accountId = account.id,
//				categoryId = null,
//			)
//		)
//
//		logcat.debug("--------------------------------------------------------------------------------")
//
//		accountRepository.getList().forEach {
//			logcat.debug(it)
//			transactionRepository.getList(it.id).forEach {
//				logcat.debug(it)
//			}
//		}
//

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