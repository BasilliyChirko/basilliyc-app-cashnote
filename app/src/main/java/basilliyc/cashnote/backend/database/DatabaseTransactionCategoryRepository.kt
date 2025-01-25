package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialTransactionCategory
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DatabaseTransactionCategoryRepository {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialTransactionCategory")
	abstract fun getListAsFlow(): Flow<List<FinancialTransactionCategory>>
	
	@Query("SELECT * FROM FinancialTransactionCategory")
	abstract suspend fun getList(): List<FinancialTransactionCategory>
	
	@Query("SELECT * FROM FinancialTransactionCategory WHERE id=:id")
	abstract suspend fun getById(id: Long): FinancialTransactionCategory?
	
	@Upsert
	abstract suspend fun save(transaction: FinancialTransactionCategory): Long
	
	@Upsert
	abstract suspend fun save(transactions: List<FinancialTransactionCategory>): List<Long>
	
	@Query("DELETE FROM FinancialTransactionCategory WHERE id=:id")
	abstract suspend fun delete(id: Long): Int
	
	@Delete
	abstract suspend fun delete(transaction: FinancialTransactionCategory): Int
	
	@Delete
	abstract suspend fun delete(transactions: List<FinancialTransactionCategory>): Int
	
}