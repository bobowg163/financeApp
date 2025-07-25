package com.xiaomai.financeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id:Long =0,
    val amount: Double,
    val type:TransactionType,
    val category:String,
    val note:String ="",
    val date: Date,
    val createdAt:Date = Date(),
    val updatedAt:Date = Date()
)

enum class TransactionType{
    INCOME,    // 收入
    EXPENSE    // 支出
}
