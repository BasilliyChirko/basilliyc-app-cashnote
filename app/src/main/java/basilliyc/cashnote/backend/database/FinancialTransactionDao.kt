package basilliyc.cashnote.backend.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialTransactionDao {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialTransaction WHERE accountId=:accountId")
	fun getListAsFlow(accountId: Long): Flow<List<FinancialTransaction>>
	
	@Query("SELECT * FROM FinancialTransaction WHERE accountId=:accountId")
	suspend fun getList(accountId: Long): List<FinancialTransaction>
	
	@Query("SELECT * FROM FinancialTransaction WHERE id=:id")
	suspend fun getById(id: Long): FinancialTransaction?
	
	@Upsert
	suspend fun save(transaction: FinancialTransaction): Long
	
	@Upsert
	suspend fun save(transactions: List<FinancialTransaction>): List<Long>
	
	@Query("DELETE FROM FinancialTransaction WHERE id=:id")
	suspend fun delete(id: Long): Int
	
	@Delete
	suspend fun delete(transaction: FinancialTransaction): Int
	
	@Delete
	suspend fun delete(transactions: List<FinancialTransaction>): Int
	
	@Query("SELECT * FROM FinancialTransaction WHERE accountId=:accountId ORDER BY date DESC")
	fun getListPagingSource(accountId: Long): PagingSource<Int, FinancialTransaction>
	
	
	@Query("SELECT * FROM FinancialTransaction WHERE accountId=:accountId AND categoryId=:categoryId AND date>=:periodStart AND date<=:periodEnd")
	suspend fun getListForStatistic(
		accountId: Long,
		categoryId: Long,
		periodStart: Long,
		periodEnd: Long,
	): List<FinancialTransaction>
	
	@Query("SELECT MIN(date) FROM FinancialTransaction WHERE accountId=:accountId AND categoryId=:categoryId")
	suspend fun getEarliestTransactionDate(
		accountId: Long,
		categoryId: Long,
	): Long
	
	
}