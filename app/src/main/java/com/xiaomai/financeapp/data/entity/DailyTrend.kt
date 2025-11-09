package com.xiaomai.financeapp.data.entity

import java.util.Date

data class DailyTrend(
    val date: Date,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)