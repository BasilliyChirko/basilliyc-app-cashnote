package basilliyc.cashnote.backend.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.FinancialCurrencyRate

@Dao
interface FinancialCurrencyRateDao {
	
	@Query("SELECT * FROM FinancialCurrencyRate")
	suspend fun getAll(): List<FinancialCurrencyRate>
	
	@Query("SELECT * FROM FinancialCurrencyRate WHERE date = :date")
	suspend fun getRates(date: Long): List<FinancialCurrencyRate>
	
	@Query("SELECT * FROM FinancialCurrencyRate WHERE date = :date AND `from` = :from AND `to` = :to")
	suspend fun getRate(date: Long, from: FinancialCurrency, to: FinancialCurrency): FinancialCurrencyRate?
	
	@Upsert
	suspend fun saveRates(rates: List<FinancialCurrencyRate>)
	
	@Query("DELETE FROM FinancialCurrencyRate")
	suspend fun deleteAll()
	
}