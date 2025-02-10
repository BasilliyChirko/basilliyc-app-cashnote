package basilliyc.cashnote

sealed class AppError(message: String, cause: Throwable? = null) : Throwable(message, cause) {
	
	sealed class Database(message: String, cause: Throwable? = null) : AppError(message, cause) {
		
		class AccountNotFound(id: Long) :
			Database("Account with id=$id not found in database")
		
		class CategoryNotFound(id: Long) :
			Database("Category with id=$id not found in database")
		
		class TransactionNotFound(id: Long) :
			Database("Transaction with id=$id not found in database")
		
		data class BackupVersionNotSupported(
			val currentVersion: Int,
			val minVersion: Int = AppValues.BackupMinVersion,
			val maxVersion: Int = AppValues.BackupMaxVersion,
		) : Database("Backup version $currentVersion is not supported. Supported from $minVersion to $maxVersion")
		
	}
	
}