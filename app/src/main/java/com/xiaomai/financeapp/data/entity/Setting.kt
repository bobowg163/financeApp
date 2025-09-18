package com.xiaomai.financeapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1L,
    var autoBackup: Boolean = false
)
