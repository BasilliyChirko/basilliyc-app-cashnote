package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialAccount
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DatabaseAccountRepository {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialAccount")
	abstract suspend fun getList(): List<FinancialAccount>
	
	@Query("SELECT * FROM FinancialAccount WHERE id=:id")
	abstract suspend fun getById(id: Long): FinancialAccount?
	
	@Upsert
	abstract suspend fun save(financialAccount: FinancialAccount): Long
	
	@Upsert
	abstract suspend fun save(financialAccounts: List<FinancialAccount>): List<Long>
	
	@Query("DELETE FROM FinancialAccount WHERE id=:id")
	abstract suspend fun delete(id: Long): Int
	
	@Delete
	abstract suspend fun delete(financialAccount: FinancialAccount): Int
	
	@Delete
	abstract suspend fun delete(financialAccounts: List<FinancialAccount>): Int
	
	@Query("SELECT * FROM FinancialAccount")
	abstract fun getListAsFlow(): Flow<List<FinancialAccount>>
	
}