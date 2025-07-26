package com.xiaomai.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.xiaomai.financeapp.data.database.AppDatabase
import com.xiaomai.financeapp.repository.TransactionRepository
import com.xiaomai.financeapp.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: TransactionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = AppDatabase.getDatabase(this, lifecycleScope)
        repository = TransactionRepository(database.transactionDao(), database.categoryDao())
        setContent {
            FinanceAppTheme {

            }
        }
    }
}

