package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import basilliyc.cashnote.data.Account
import basilliyc.cashnote.data.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AppDatabaseTransactionRepository {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM `Transaction` WHERE accountId=:accountId")
	abstract fun getListAsFlow(accountId: Long): Flow<List<Transaction>>
	
	@Query("SELECT * FROM `Transaction` WHERE accountId=:accountId")
	abstract suspend fun getList(accountId: Long): List<Transaction>
	
	@Query("SELECT * FROM `Transaction` WHERE id=:id")
	abstract suspend fun getById(id: Long): Transaction?
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract suspend fun insert(transaction: Transaction): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract suspend fun insert(transactions: List<Transaction>): List<Long>
	
	@Query("DELETE FROM Account WHERE id=:id")
	abstract suspend fun delete(id: Long): Int
	
	@Delete
	abstract suspend fun delete(transaction: Transaction): Int
	
	@Delete
	abstract suspend fun delete(transactions: List<Transaction>): Int
	
}