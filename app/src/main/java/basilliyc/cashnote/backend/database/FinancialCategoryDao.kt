package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialCategoryDao {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialCategory ORDER BY position")
	fun getListAsFlow(): Flow<List<FinancialCategory>>
	
	@Query("SELECT * FROM FinancialCategory ORDER BY position")
	suspend fun getList(): List<FinancialCategory>
	
	@Query("SELECT * FROM FinancialCategory WHERE id=:id")
	suspend fun getById(id: Long): FinancialCategory?
	
	@Query("SELECT * FROM FinancialCategory WHERE id=:id")
	fun getByIdAsFlow(id: Long): Flow<FinancialCategory?>
	
	@Upsert
	suspend fun save(transaction: FinancialCategory): Long
	
	@Upsert
	suspend fun save(transactions: List<FinancialCategory>): List<Long>
	
	@Query("DELETE FROM FinancialCategory WHERE id=:id")
	suspend fun delete(id: Long): Int
	
	@Delete
	suspend fun delete(transaction: FinancialCategory): Int
	
	@Delete
	suspend fun delete(transactions: List<FinancialCategory>): Int
	
	@Query("DELETE FROM FinancialCategory")
	suspend fun deleteAll()
	
	//----------------------------------------------------------------------------------------------
	//  OTHER
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT COUNT(position) FROM FinancialCategory")
	suspend fun getItemsCount(): Int
	
	@Query("SELECT MAX(position) FROM FinancialCategory")
	suspend fun getMaxPosition(): Int
	
	@Query("SELECT * FROM FinancialCategory WHERE id IN ( SELECT categoryId FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId AND visible=1 )")
	suspend fun getListVisibleInAccount(accountId: Long): List<FinancialCategory>
	
	@Query("SELECT * FROM FinancialCategory WHERE id IN ( SELECT categoryId FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId AND visible=1 )")
	fun getListVisibleInAccountAsFlow(accountId: Long): Flow<List<FinancialCategory>>
	
}