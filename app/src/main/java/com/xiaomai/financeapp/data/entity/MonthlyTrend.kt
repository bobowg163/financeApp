package com.xiaomai.financeapp.data.entity

data class MonthlyTrend(
    val year: Int,
    val month: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)