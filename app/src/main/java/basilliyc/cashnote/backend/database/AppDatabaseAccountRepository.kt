package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import basilliyc.cashnote.data.Account
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AppDatabaseAccountRepository {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM Account")
	abstract suspend fun getList(): List<Account>
	
	@Query("SELECT * FROM Account WHERE id=:id")
	abstract suspend fun getById(id: Long): Account?
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract suspend fun insert(account: Account): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract suspend fun insert(accounts: List<Account>): List<Long>
	
	@Query("DELETE FROM Account WHERE id=:id")
	abstract suspend fun delete(id: Long): Int
	
	@Delete
	abstract suspend fun delete(account: Account): Int
	
	@Delete
	abstract suspend fun delete(accounts: List<Account>): Int
	
	@Query("SELECT * FROM Account")
	abstract fun getListAsFlow(): Flow<List<Account>>
	
}