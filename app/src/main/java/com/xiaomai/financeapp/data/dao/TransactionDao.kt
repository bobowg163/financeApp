package com.xiaomai.financeapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    suspend fun getTotalByType(type: TransactionType): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): Double?

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = :type GROUP BY category ORDER BY total DESC")
    suspend fun getCategoryTotals(type: TransactionType): List<CategoryTotal>

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate GROUP BY category ORDER BY total DESC")
    suspend fun getCategoryTotalsByDateRange(
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): List<CategoryTotal>

    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
    
    @Query("""
        SELECT 
            strftime('%Y', date/1000, 'unixepoch', 'localtime') as year,
            strftime('%m', date/1000, 'unixepoch', 'localtime') as month,
            SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as totalIncome,
            SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as totalExpense,
            (SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) - 
             SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END)) as balance
        FROM transactions 
        GROUP BY year, month 
        ORDER BY year DESC, month DESC
        LIMIT 12
    """)
    suspend fun getMonthlyTrends(): List<MonthlyTrendData>
    
    @Query("""
        SELECT 
            date,
            SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as totalIncome,
            SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as totalExpense,
            (SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) - 
             SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END)) as balance
        FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY DATE(date/1000, 'unixepoch', 'localtime') 
        ORDER BY date ASC
    """)
    suspend fun getDailyTrends(startDate: Date, endDate: Date): List<DailyTrendData>
}

data class CategoryTotal(
    val category: String,
    val total: Double
)

data class MonthlyTrendData(
    val year: String,
    val month: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)

data class DailyTrendData(
    val date: Date,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)