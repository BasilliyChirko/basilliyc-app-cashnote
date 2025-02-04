package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialAccountDao {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM FinancialAccount ORDER BY position ASC")
	suspend fun getList(): List<FinancialAccount>
	
	@Query("SELECT * FROM FinancialAccount WHERE id=:id")
	suspend fun getById(id: Long): FinancialAccount?
	
	@Upsert
	suspend fun save(financialAccount: FinancialAccount): Long
	
	@Upsert
	suspend fun save(financialAccounts: List<FinancialAccount>): List<Long>
	
	@Query("DELETE FROM FinancialAccount WHERE id=:id")
	suspend fun delete(id: Long)
	
	@Delete
	suspend fun delete(financialAccount: FinancialAccount)
	
	@Delete
	suspend fun delete(financialAccounts: List<FinancialAccount>)
	
	@Query("SELECT * FROM FinancialAccount ORDER BY position ASC")
	fun getListAsFlow(): Flow<List<FinancialAccount>>
	
	@Query("SELECT * FROM FinancialAccount WHERE id=:id")
	fun getByIdAsFlow(id: Long): Flow<FinancialAccount?>
	
	//----------------------------------------------------------------------------------------------
	//  OTHER
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT COUNT(position) FROM FinancialAccount")
	suspend fun getItemsCount(): Int
	
	@Query("SELECT MAX(position) FROM FinancialAccount")
	suspend fun getMaxPosition(): Int
	
}