package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialStatistic
import basilliyc.cashnote.data.FinancialStatisticParams
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialStatisticDao {
	
	//----------------------------------------------------------------------------------------------
	//  STATISTIC PARAMS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialStatisticParams LIMIT 1")
	suspend fun getParams(): FinancialStatisticParams?
	
	@Query("SELECT * FROM FinancialStatisticParams LIMIT 1")
	fun getParamsAsFlow(): Flow<FinancialStatisticParams?>
	
	@Upsert
	suspend fun saveParams(params: FinancialStatisticParams)
	
	//----------------------------------------------------------------------------------------------
	//  STATISTIC
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialStatistic WHERE accountId=:accountId")
	fun getListForAccountAsFlow(accountId: Long): Flow<List<FinancialStatistic>>
	
	@Query("SELECT * FROM FinancialStatistic WHERE accountId=:accountId")
	suspend fun getListForAccount(accountId: Long): List<FinancialStatistic>
	
	@Query("SELECT * FROM FinancialStatistic WHERE categoryId=:categoryId")
	fun getListForCategoryAsFlow(categoryId: Long): Flow<List<FinancialStatistic>>
	
	@Query("SELECT * FROM FinancialStatistic WHERE categoryId=:categoryId")
	suspend fun getListForCategory(categoryId: Long): List<FinancialStatistic>
	
	@Query("SELECT * FROM FinancialStatistic WHERE accountId=:accountId AND categoryId=:categoryId")
	suspend fun getById(accountId: Long, categoryId: Long): FinancialStatistic?
	
	@Upsert
	suspend fun save(transaction: FinancialStatistic): Long
	
	@Upsert
	suspend fun save(transactions: List<FinancialStatistic>): List<Long>
	
	@Query("DELETE FROM FinancialStatistic WHERE accountId=:accountId AND categoryId=:categoryId")
	suspend fun delete(accountId: Long, categoryId: Long): Int
	
	@Delete
	suspend fun delete(transaction: FinancialStatistic): Int
	
	@Delete
	suspend fun delete(transactions: List<FinancialStatistic>): Int
	
	@Query("DELETE FROM FinancialStatistic")
	suspend fun deleteAll(): Int
	
}