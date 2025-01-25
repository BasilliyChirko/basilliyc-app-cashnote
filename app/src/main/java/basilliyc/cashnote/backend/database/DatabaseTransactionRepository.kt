package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialTransaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DatabaseTransactionRepository {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialTransaction WHERE accountId=:accountId")
	abstract fun getListAsFlow(accountId: Long): Flow<List<FinancialTransaction>>
	
	@Query("SELECT * FROM FinancialTransaction WHERE accountId=:accountId")
	abstract suspend fun getList(accountId: Long): List<FinancialTransaction>
	
	@Query("SELECT * FROM FinancialTransaction WHERE id=:id")
	abstract suspend fun getById(id: Long): FinancialTransaction?
	
	@Upsert
	abstract suspend fun save(transaction: FinancialTransaction): Long
	
	@Upsert
	abstract suspend fun save(transactions: List<FinancialTransaction>): List<Long>
	
	@Query("DELETE FROM FinancialTransaction WHERE id=:id")
	abstract suspend fun delete(id: Long): Int
	
	@Delete
	abstract suspend fun delete(transaction: FinancialTransaction): Int
	
	@Delete
	abstract suspend fun delete(transactions: List<FinancialTransaction>): Int
	
}