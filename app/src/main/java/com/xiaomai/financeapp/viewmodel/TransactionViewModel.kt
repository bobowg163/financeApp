package com.xiaomai.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xiaomai.financeapp.data.dao.CategoryTotal
import com.xiaomai.financeapp.data.entity.Category
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.data.entity.TransactionType
import com.xiaomai.financeapp.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * 项目: financeApp
 * 包名: com.xiaomai.financeapp.viewmodel
 * 作者: bobowg
 * 日期: 2025/8/16 时间: 16:04
 * 备注：
 **/

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    val allTransactions: Flow<List<Transaction>> = repository.getAllTransaction()
    val allCategoires: Flow<List<Category>> = repository.getAllCategories()

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> =
        repository.getTransactionsByDateRange(startDate, endDate)

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> =
        repository.getCategoriesByType(type)

    fun installTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun loadStatistics(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            val totalInCome =
                repository.getTotalByTypeAndDateRange(TransactionType.INCOME, startDate, endDate)
            val totalExpense =
                repository.getTotalByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate)
            val incomeCategoryTotals =
                repository.getCategoryTotalsByDateRange(TransactionType.INCOME, startDate, endDate)
            val expenseCategoryTotals = repository.getCategoryTotalsByDateRange(
                type = TransactionType.EXPENSE,
                startDate,
                endDate
            )
            _uiState.value = _uiState.value.copy(
                totalIncome = totalInCome,
                totalExpense = totalExpense,
                balance = totalInCome - totalExpense,
                incomeCategoryTotals = incomeCategoryTotals,
                expenseCategoryTotals = expenseCategoryTotals
            )
        }
    }

    fun loadCurrentMonthStatistics() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.time

        loadStatistics(startDate, endDate)
    }

    fun setSelectedDateRange(startDate: Date, endDate: Date) {
        _uiState.value = _uiState.value.copy(
            selectedStartDate = startDate,
            selectedEndDate = endDate
        )
        loadStatistics(startDate, endDate)
    }

    class Factory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}

data class TransactionUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val incomeCategoryTotals: List<CategoryTotal> = emptyList(),
    val expenseCategoryTotals: List<CategoryTotal> = emptyList(),
    val selectedStartDate: Date = Date(),
    val selectedEndDate: Date = Date(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)