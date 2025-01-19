package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import basilliyc.cashnote.data.Account

@Dao
abstract class AppDatabaseAccountRepository {
	
	//----------------------------------------------------------------------------------------------
	//  BASIC CRUD METHODS
	//----------------------------------------------------------------------------------------------
	
	@Query("SELECT * FROM Account")
	abstract suspend fun getAccountsList(): List<Account>
	
	@Query("SELECT * FROM Account WHERE id=:id")
	abstract suspend fun getAccount(id: Long): Account?
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract suspend fun saveAccount(account: Account): Long
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract suspend fun saveAccount(accounts: List<Account>): List<Long>
	
	@Query("DELETE FROM Account WHERE id=:id")
	abstract suspend fun deleteAccount(id: Long): Int
	
	@Delete
	abstract suspend fun deleteAccount(account: Account): Int
	
	@Delete
	abstract suspend fun deleteAccount(accounts: List<Account>): Int
	
}