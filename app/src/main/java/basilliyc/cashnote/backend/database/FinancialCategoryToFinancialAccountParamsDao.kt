package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialCategoryToFinancialAccountParams
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialCategoryToFinancialAccountParamsDao {
	
	@Query("SELECT * FROM FinancialCategoryToFinancialAccountParams")
	suspend fun getList(): List<FinancialCategoryToFinancialAccountParams>
	
	@Query("SELECT * FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId")
	suspend fun getListByAccountId(accountId: Long): List<FinancialCategoryToFinancialAccountParams>
	
	@Query("SELECT * FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId")
	fun getListByAccountIdAsFlow(accountId: Long): Flow<List<FinancialCategoryToFinancialAccountParams>>
	
	@Query("SELECT * FROM FinancialCategoryToFinancialAccountParams WHERE categoryId=:categoryId")
	suspend fun getListByCategoryId(categoryId: Long): List<FinancialCategoryToFinancialAccountParams>
	
	@Query("SELECT * FROM FinancialCategoryToFinancialAccountParams WHERE categoryId=:categoryId")
	fun getListByCategoryIdAsFlow(categoryId: Long): Flow<List<FinancialCategoryToFinancialAccountParams>>
	
	@Query("SELECT * FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId AND categoryId=:categoryId")
	suspend fun get(accountId: Long, categoryId: Long): FinancialCategoryToFinancialAccountParams?
	
	@Query("SELECT * FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId AND categoryId=:categoryId")
	fun getAsFlow(accountId: Long, categoryId: Long): Flow<FinancialCategoryToFinancialAccountParams?>
	
	@Query("DELETE FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId AND categoryId=:categoryId")
	suspend fun delete(accountId: Long, categoryId: Long): Int
	
	@Delete
	suspend fun delete(transaction: FinancialCategoryToFinancialAccountParams): Int
	
	@Delete
	suspend fun delete(transactions: List<FinancialCategoryToFinancialAccountParams>): Int
	
	@Query("DELETE FROM FinancialCategoryToFinancialAccountParams WHERE accountId=:accountId")
	suspend fun deleteByAccountId(accountId: Long): Int
	
	@Query("DELETE FROM FinancialCategoryToFinancialAccountParams WHERE categoryId=:categoryId")
	suspend fun deleteByCategoryId(categoryId: Long): Int
	
	@Query("DELETE FROM FinancialCategoryToFinancialAccountParams")
	suspend fun deleteAll(): Int
	
	@Upsert
	suspend fun save(transaction: FinancialCategoryToFinancialAccountParams): Long
	
	@Upsert
	suspend fun save(transactions: List<FinancialCategoryToFinancialAccountParams>): List<Long>
	
}