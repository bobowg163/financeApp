package com.xiaomai.financeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id:Long =0,
    val name:String,
    val type:TransactionType,
    val color:String = "#FF6B6B", // 默认颜色
    val icon:String = "\uD83D\uDCB0", // 默认图标
    val isDefault: Boolean = false
)
