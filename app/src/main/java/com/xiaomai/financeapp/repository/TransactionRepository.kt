package com.xiaomai.financeapp.repository

import com.xiaomai.financeapp.data.dao.CategoryDao
import com.xiaomai.financeapp.data.dao.CategoryTotal
import com.xiaomai.financeapp.data.dao.TransactionDao
import com.xiaomai.financeapp.data.entity.Category
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun getTransactionById(id: Long): Transaction? = transactionDao.getTransactionById(id)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type)

    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(category)

    suspend fun getTotalByType(type: TransactionType): Double =
        transactionDao.getTotalByType(type) ?: 0.0

    suspend fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Double =
        transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate) ?: 0.0

    suspend fun getCategoryTotals(type: TransactionType): List<CategoryTotal> =
        transactionDao.getCategoryTotals(type)

    suspend fun getCategoryTotalsByDateRange(type: TransactionType, startDate: Date, endDate: Date): List<CategoryTotal> =
        transactionDao.getCategoryTotalsByDateRange(type, startDate, endDate)

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteAllTransactions() = transactionDao.deleteAllTransactions()

    // Category operations
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.getCategoriesByType(type)

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)

    suspend fun getCategoryByNameAndType(name: String, type: TransactionType): Category? =
        categoryDao.getCategoryByNameAndType(name, type)

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    suspend fun deleteNonDefaultCategories() = categoryDao.deleteNonDefaultCategories()

}