package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialCategory
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DatabaseTransactionCategoryRepository {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialCategory ORDER BY position")
	abstract fun getListAsFlow(): Flow<List<FinancialCategory>>
	
	@Query("SELECT * FROM FinancialCategory ORDER BY position")
	abstract suspend fun getList(): List<FinancialCategory>
	
	@Query("SELECT * FROM FinancialCategory WHERE id=:id")
	abstract suspend fun getById(id: Long): FinancialCategory?
	
	@Upsert
	abstract suspend fun save(transaction: FinancialCategory): Long
	
	@Upsert
	abstract suspend fun save(transactions: List<FinancialCategory>): List<Long>
	
	@Query("DELETE FROM FinancialCategory WHERE id=:id")
	abstract suspend fun delete(id: Long): Int
	
	@Delete
	abstract suspend fun delete(transaction: FinancialCategory): Int
	
	@Delete
	abstract suspend fun delete(transactions: List<FinancialCategory>): Int
	
	//----------------------------------------------------------------------------------------------
	//  OTHER
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT COUNT(position) FROM FinancialCategory")
	abstract suspend fun getItemsCount(): Int
	
	@Query("SELECT MAX(position) FROM FinancialCategory")
	abstract suspend fun getMaxPosition(): Int
	
}